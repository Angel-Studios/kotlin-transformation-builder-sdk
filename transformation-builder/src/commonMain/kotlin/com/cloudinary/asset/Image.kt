package com.cloudinary.asset

import com.cloudinary.transformation.Action
import com.cloudinary.transformation.Format
import com.cloudinary.transformation.ITransformable
import com.cloudinary.transformation.ImageTransformation

class Image(
    // config
    baseUrl: String,

    // fields
    version: String? = null,
    publicId: String? = null,
    extension: Format? = null,
    urlSuffix: String? = null,
    deliveryType: String? = null,
    private val transformation: ImageTransformation? = null
) : BaseAsset(
    baseUrl,
    version,
    publicId,
    extension,
    urlSuffix,
    ASSET_TYPE_IMAGE,
    deliveryType,
) {
    class Builder(baseUrl: String) :
        BaseAssetBuilder(baseUrl, ASSET_TYPE_IMAGE), ITransformable<Builder> {

        private var transformation: ImageTransformation? = null

        fun transformation(transformation: ImageTransformation) = apply { this.transformation = transformation }
        fun transformation(transform: ImageTransformation.Builder.() -> Unit) = apply {
            val builder = ImageTransformation.Builder()
            builder.transform()
            this.transformation = builder.build()
        }

        override fun add(action: Action) = apply {
            this.transformation = (this.transformation ?: ImageTransformation()).add(action)
        }

        fun build() = Image(
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
