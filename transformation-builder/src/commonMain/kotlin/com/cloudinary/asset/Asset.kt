package com.cloudinary.asset

import com.cloudinary.transformation.*
import com.cloudinary.util.URLDecoder
import com.cloudinary.util.cldIsHttpUrl
import com.cloudinary.util.cldMergeSlashedInUrl
import com.cloudinary.util.cldSmartUrlEncode

internal const val DEFAULT_ASSET_TYPE = "image"
internal const val DEFAULT_DELIVERY_TYPE = "upload"

class Asset(
    // config
    baseUrl: String,

    // fields
    private val transformation: Transformation? = null
) : BaseAsset(
    baseUrl,
) {

    override fun getTransformationString() = transformation?.toString()

    class Builder(
        baseUrl: String,
    ) : BaseAssetBuilder(baseUrl), ITransformable<Builder> {

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
            transformation
        )
    }
}

@TransformationDsl
abstract class BaseAsset constructor(
    private val baseUrl: String,
) {
    fun generate(
        id: String,
        format: Format,
    ): String {
        var mutableSource = id
        val finalizedSource = finalizeSource(mutableSource, format)

        mutableSource = finalizedSource.source

        val transformationString = getTransformationString()

        return listOfNotNull(
            baseUrl,
            transformationString,
            mutableSource
        ).joinToString("/").cldMergeSlashedInUrl()
    }

    abstract fun getTransformationString(): String?

    @TransformationDsl
    abstract class BaseAssetBuilder
    internal constructor(
        protected val baseUrl: String,
        protected var extension: Format? = null,
    ) {

        protected var version: String? = null
        protected var publicId: String? = null

        fun version(version: String) = apply { this.version = version }
        fun publicId(publicId: String) = apply { this.publicId = publicId }
        fun extension(extension: Format) = apply { this.extension = extension }
        fun extension(extension: String) = apply { this.extension = Format.custom(extension) }
    }
}

private fun finalizeSource(
    source: String,
    extension: Any?
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
        if (extension != null) {
            mutableSource = "$mutableSource.$extension"
            sourceToSign = "$sourceToSign.$extension"
        }
    }

    return FinalizedSource(mutableSource, sourceToSign)
}

private fun finalizeResourceType(
    resourceType: String?,
    urlSuffix: String?
): String? {
    var mutableResourceType: String? = resourceType ?: DEFAULT_ASSET_TYPE
    var mutableType: String? = DEFAULT_DELIVERY_TYPE

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
