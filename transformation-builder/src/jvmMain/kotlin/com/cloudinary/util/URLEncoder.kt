package com.cloudinary.util

import java.net.URLEncoder

actual class URLEncoder {
    actual companion object {
        actual fun encode(input: String, encoding: String): String = URLEncoder.encode(input, encoding)
    }
}