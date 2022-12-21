package com.cloudinary.util

expect class URLEncoder {
    companion object {
        fun encode(input: String, encoding: String = "UTF-8"): String
    }
}
