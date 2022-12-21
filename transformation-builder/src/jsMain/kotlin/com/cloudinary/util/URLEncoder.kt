package com.cloudinary.util

external fun encodeURI(encodedURI: String): String

actual class URLEncoder {
    actual companion object {
        actual fun encode(input: String, encoding: String): String = encodeURI(input)
    }
}
