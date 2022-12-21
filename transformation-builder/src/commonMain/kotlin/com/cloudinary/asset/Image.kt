package com.cloudinary.asset

import com.cloudinary.transformation.Action
import com.cloudinary.transformation.ITransformable
import com.cloudinary.transformation.ImageTransformation

class Image(
    // config
    baseUrl: String,

    // fields
    private val transformation: ImageTransformation? = null
) : BaseAsset(
    baseUrl,
) {
    class Builder(baseUrl: String) :
        BaseAssetBuilder(baseUrl), ITransformable<Builder> {

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
            transformation
        )
    }

    override fun getTransformationString() = transformation?.toString()
}
