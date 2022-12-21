package com.cloudinary.util

expect class URLDecoder {
    companion object {
        fun decode(input: String, encoding: String = "UTF-8"): String
    }
}
