package com.cloudinary

import com.cloudinary.asset.Asset
import com.cloudinary.asset.Image
import com.cloudinary.asset.Video

class Cloudinary(val baseUrl: String) {
    fun raw(options: (Asset.Builder.() -> Unit)? = null): Asset {
        val builder = Asset.Builder(baseUrl)
        options?.let { builder.it() }
        return builder.build()
    }

    fun image(options: (Image.Builder.() -> Unit)? = null): Image {
        val builder = Image.Builder(baseUrl)
        options?.let { builder.it() }
        return builder.build()
    }

    fun video(options: (Video.Builder.() -> Unit)? = null): Video {
        val builder = Video.Builder(baseUrl)
        options?.let { builder.it() }
        return builder.build()
    }
}
