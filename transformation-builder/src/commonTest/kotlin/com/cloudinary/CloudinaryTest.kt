package com.cloudinary

import com.cloudinary.transformation.Format
import com.cloudinary.transformation.resize.Resize
import kotlin.test.Test
import kotlin.test.assertEquals

class CloudinaryTest {

    @Test
    fun cloudinaryTransformation() {
        val cloudinary = Cloudinary(BASE_URL)
        val result = cloudinary.image {
            resize(Resize.fill {
                width(100)
                height(200)
                aspectRatio(2)
            })
        }.getTransformationString()
        assertEquals("ar_2,c_fill,h_200,w_100", result)
    }

    @Test
    fun cloudinaryGenerate() {
        val cloudinary = Cloudinary(BASE_URL)
        val result = cloudinary.image {
            resize(Resize.fill {
                width(100)
                height(200)
                aspectRatio(2)
            })
        }.generate("v1632763262/angel-app/freelancers/discovery_images/poster", Format.png())
        println(result)
    }

    companion object {
        private const val BASE_URL = "https://images.angelstudios.com/image/upload"
    }
}
