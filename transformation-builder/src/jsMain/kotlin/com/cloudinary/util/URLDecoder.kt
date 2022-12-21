package com.cloudinary.util

external fun decodeURI(encodedURI: String): String

actual class URLDecoder {
    actual companion object {
        actual fun decode(input: String, encoding: String): String = decodeURI(input)
    }
}
