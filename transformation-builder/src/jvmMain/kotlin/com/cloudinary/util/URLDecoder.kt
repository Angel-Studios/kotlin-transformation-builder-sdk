package com.cloudinary.util

import java.net.URLDecoder

actual class URLDecoder {
    actual companion object {
        actual fun decode(input: String, encoding: String): String = URLDecoder.decode(input, encoding)
    }
}