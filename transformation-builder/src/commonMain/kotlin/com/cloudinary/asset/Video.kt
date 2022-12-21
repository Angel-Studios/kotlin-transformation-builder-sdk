package com.cloudinary.asset

import com.cloudinary.transformation.Action
import com.cloudinary.transformation.ITransformable
import com.cloudinary.transformation.VideoTransformation


class Video(
    // config
    baseUrl: String,

    // fields
    private val transformation: VideoTransformation? = null
) : BaseAsset(
    baseUrl,
) {
    class Builder(baseUrl: String) : BaseAssetBuilder(baseUrl), ITransformable<Builder> {

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
            transformation
        )
    }

    override fun getTransformationString() = transformation?.toString()
}
