package com.cloudinary.asset

import com.cloudinary.transformation.Action
import com.cloudinary.transformation.Format
import com.cloudinary.transformation.ITransformable
import com.cloudinary.transformation.VideoTransformation

class Video(
    // config
    baseUrl: String,

    // fields
    version: String? = null,
    publicId: String? = null,
    extension: Format? = null,
    urlSuffix: String? = null,
    deliveryType: String? = null,
    private val transformation: VideoTransformation? = null
) : BaseAsset(
    baseUrl,
    version,
    publicId,
    extension,
    urlSuffix,
    ASSET_TYPE_VIDEO,
    deliveryType,
) {
    class Builder(baseUrl: String) :
        BaseAssetBuilder(baseUrl, ASSET_TYPE_VIDEO), ITransformable<Builder> {

        private var transformation: VideoTransformation? = null

        fun transformation(transformation: VideoTransformation) = apply { this.transformation = transformation }
        fun transformation(transform: VideoTransformation.Builder.() -> Unit) = apply {
            val builder = VideoTransformation.Builder()
            builder.transform()
            this.transformation = builder.build()
        }

        override fun add(action: Action) = apply {
            this.transformation = (this.transformation ?: VideoTransformation()).add(action)
        }

        fun build() = Video(
            baseUrl,
            version,
            publicId,
            extension,
            urlSuffix,
            deliveryType,
            transformation
        )
    }

    override fun getTransformationString() = transformation?.toString()
}
