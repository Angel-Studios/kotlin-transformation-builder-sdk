package com.cloudinary.asset

import com.cloudinary.transformation.*
import com.cloudinary.util.*

internal const val DEFAULT_ASSET_TYPE = "image"
internal const val DEFAULT_DELIVERY_TYPE = "upload"

const val ASSET_TYPE_IMAGE = "image"
const val ASSET_TYPE_VIDEO = "video"

class Asset(
    // config
    baseUrl: String,

    // fields
    version: String? = null,
    publicId: String? = null,
    extension: Format? = null,
    urlSuffix: String? = null,
    assetType: String = DEFAULT_ASSET_TYPE,
    deliveryType: String? = null,
    private val transformation: Transformation? = null
) : BaseAsset(
    baseUrl,
    version,
    publicId,
    extension,
    urlSuffix,
    assetType,
    deliveryType,
) {

    override fun getTransformationString() = transformation?.toString()

    class Builder(
        baseUrl: String,
        assetType: String = DEFAULT_ASSET_TYPE
    ) : BaseAssetBuilder(baseUrl, assetType), ITransformable<Builder> {
        private var transformation: Transformation? = null

        fun transformation(transformation: Transformation) =
            apply { this.transformation = transformation }

        fun transformation(transform: Transformation.Builder.() -> Unit) = apply {
            val builder = Transformation.Builder()
            builder.transform()
            this.transformation = builder.build()
        }

        override fun add(action: Action) = apply {
            this.transformation = (this.transformation ?: Transformation()).add(action)
        }

        fun build() = Asset(
            baseUrl,
            version,
            publicId,
            extension,
            urlSuffix,
            assetType,
            deliveryType,
            transformation
        )
    }
}

@TransformationDsl
abstract class BaseAsset constructor(
    // config
    private val baseUrl: String,

    // fields
    private val version: String? = null,
    private val publicId: String? = null,
    private val extension: Format? = null,
    private val urlSuffix: String? = null,
    private val assetType: String = DEFAULT_ASSET_TYPE,
    private val deliveryType: String? = null,
) {
    fun generate(source: String? = null): String? {
        var mutableSource = source ?: publicId ?: return null
        val httpSource = mutableSource.cldIsHttpUrl()

        if (httpSource && ((deliveryType.isNullOrBlank() || deliveryType == "asset"))) {
            return mutableSource
        }

        val finalizedSource = finalizeSource(mutableSource, extension, urlSuffix)

        mutableSource = finalizedSource.source
        val sourceToSign = finalizedSource.sourceToSign

        var mutableVersion = version
        if (sourceToSign.contains("/") && !sourceToSign.cldHasVersionString() &&
            !httpSource && mutableVersion.isNullOrBlank()
        ) {
            mutableVersion = "1"
        }

        mutableVersion = if (mutableVersion == null) "" else "v$mutableVersion"

        val transformationString = getTransformationString()
        val finalizedResourceType = finalizeResourceType(
            assetType,
            deliveryType,
            urlSuffix,
        )

        return listOfNotNull(
            baseUrl,
            finalizedResourceType,
            transformationString,
            mutableVersion,
            mutableSource
        ).joinToString("/").cldMergeSlashedInUrl()
    }

    abstract fun getTransformationString(): String?

    @TransformationDsl
    abstract class BaseAssetBuilder
    internal constructor(
        protected val baseUrl: String,
        protected var assetType: String = DEFAULT_ASSET_TYPE
    ) {

        protected var version: String? = null
        protected var publicId: String? = null
        protected var extension: Format? = null
        protected var urlSuffix: String? = null
        var deliveryType: String? = null

        fun version(version: String) = apply { this.version = version }
        fun publicId(publicId: String) = apply { this.publicId = publicId }
        fun extension(extension: Format) = apply { this.extension = extension }
        fun urlSuffix(urlSuffix: String) = apply { this.urlSuffix = urlSuffix }
        fun deliveryType(deliveryType: String) = apply { this.deliveryType = deliveryType }
        fun assetType(assetType: String) = apply { this.assetType = assetType }
    }
}

private fun finalizeSource(
    source: String,
    extension: Format?,
    urlSuffix: String?
): FinalizedSource {
    var mutableSource = source.cldMergeSlashedInUrl()
    var sourceToSign: String
    if (mutableSource.cldIsHttpUrl()) {
        mutableSource = mutableSource.cldSmartUrlEncode()
        sourceToSign = mutableSource
    } else {
        mutableSource = try {
            URLDecoder.decode(mutableSource.replace("+", "%2B"), "UTF-8").cldSmartUrlEncode()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        sourceToSign = mutableSource
        if (!urlSuffix.isNullOrBlank()) {
            require(!(urlSuffix.contains(".") || urlSuffix.contains("/"))) { "url_suffix should not include . or /" }
            mutableSource = "$mutableSource/$urlSuffix"
        }
        if (extension != null) {
            mutableSource = "$mutableSource.$extension"
            sourceToSign = "$sourceToSign.$extension"
        }
    }

    return FinalizedSource(mutableSource, sourceToSign)
}

private fun finalizeResourceType(
    resourceType: String?,
    type: String?,
    urlSuffix: String?,
): String? {
    var mutableResourceType: String? = resourceType ?: DEFAULT_ASSET_TYPE
    var mutableType: String? = type ?: DEFAULT_DELIVERY_TYPE

    if (!urlSuffix.isNullOrBlank()) {
        if (mutableResourceType == "image" && mutableType == "upload") {
            mutableResourceType = "images"
            mutableType = null
        } else if (mutableResourceType == "image" && mutableType == "private") {
            mutableResourceType = "private_images"
            mutableType = null
        } else if (mutableResourceType == "image" && mutableType == "authenticated") {
            mutableResourceType = "authenticated_images"
            mutableType = null
        } else if (mutableResourceType == "raw" && mutableType == "upload") {
            mutableResourceType = "files"
            mutableType = null
        } else if (mutableResourceType == "video" && mutableType == "upload") {
            mutableResourceType = "videos"
            mutableType = null
        } else {
            throw IllegalArgumentException("URL Suffix only supported for image/upload, image/private, raw/upload, " +
                    "image/authenticated  and video/upload")
        }
    }
    var result = mutableResourceType
    if (mutableType != null) {
        result += "/$mutableType"
    }

    return result
}

private class FinalizedSource(val source: String, val sourceToSign: String)
