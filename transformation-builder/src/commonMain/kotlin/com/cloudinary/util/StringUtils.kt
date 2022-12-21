package com.cloudinary.util

import com.chrynan.uri.core.Uri
import com.cloudinary.util.Base64Coder.encodeString
import com.cloudinary.util.Base64Coder.encodeURLSafeString

/**
 * If the param is null the original string is returned unchanged.
 */
internal fun String.cldJoinWithOrReturnOriginal(separator: String, toJoin: Any?): String {
    return if (toJoin != null) this + separator + toJoin.toString() else this
}

/**
 * Checks whether this string is a valid http url.
 */
internal fun String.cldIsHttpUrl() = startsWith("https:/", true) || startsWith("http:/", true)

/**
 * Checks whether this url string has a version component in it (e.g. 'v12345')
 */
internal fun String.cldHasVersionString(): Boolean {
    var inVersion = false
    for (i in indices) {
        val c = get(i)
        inVersion = if (c == 'v') {
            true
        } else if (c.isDigit() && inVersion) {
            return true
        } else {
            false
        }
    }

    return false
}

/**
 * Returns this string with all duplicate slashes merged into a single slash (e.g. "abc///efg -> "abc/efg")
 */
internal fun String.cldMergeSlashedInUrl(): String {
    val builder = StringBuilder()
    var prevIsColon = false
    var inMerge = false
    for (i in indices) {
        val c = get(i)
        if (c == ':') {
            prevIsColon = true
            builder.append(c)
        } else {
            if (c == '/') {
                inMerge = if (prevIsColon) {
                    builder.append(c)
                    false
                } else {
                    if (!inMerge) {
                        builder.append(c)
                    }
                    true
                }
            } else {
                inMerge = false
                builder.append(c)
            }

            prevIsColon = false
        }
    }

    return builder.toString()
}

/**
 * Merge all consecutive underscores and spaces into a single underscore, e.g. "ab___c_  _d" becomes "ab_c_d"
 */
internal fun String.cldMergeToSingleUnderscore(): String {
    val buffer = StringBuilder()
    var inMerge = false
    for (element in this) {
        if (element == ' ' || element == '_') {
            if (!inMerge) {
                buffer.append('_')
            }
            inMerge = true

        } else {
            inMerge = false
            buffer.append(element)
        }
    }

    return buffer.toString()
}

/**
 * Encodes the url for Cloudinary use (standard url encoding + some custom replacements).
 */
internal fun String.cldSmartUrlEncode() = URLEncoder.encode(this, "UTF-8")
    .replace("%2F", "/")
    .replace("%3A", ":")
    .replace("+", "%20")


/**
 *
 */
internal fun Uri.cldQueryAsMap() =
    query?.split("&")?.associate { it.split("=").run { Pair(this[0], this[1]) } } ?: emptyMap()

/**
 * Returns the base64 representation of this string
 */
internal fun String.cldToBase64(): String = encodeString(this)

/**
 * Returns the url-safe variant of the base64 representation of this string
 */
internal fun String.cldToUrlSafeBase64() = encodeURLSafeString(this)

/**
 * Encodes public id to be used in urls (such as wasm asset or layers)
 */
internal fun String.cldEncodePublicId() = replace('/', ':').replace(",", "%2c")


internal fun String.cldHexStringToByteArray(): ByteArray {
    val len: Int = this.length
    val data = ByteArray(len / 2)

    require(len % 2 == 0) { "Length of string to parse must be even." }

    var i = 0
    while (i < len) {
        data[i / 2] = ((get(i).digitToInt(16) shl 4) + get(i + 1).digitToInt(16)).toByte()
        i += 2
    }

    return data
}

private val HEX_ARRAY = "0123456789abcdef".toCharArray()
private val camelCaseRegex = Regex("""[A-Z]""")

fun ByteArray.toHex(): String {
    val hexChars = CharArray(size * 2)
    for (j in indices) {
        val v = get(j).toInt() and 0xFF
        hexChars[j * 2] = HEX_ARRAY[v.ushr(4)]
        hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }

    return hexChars.toString()
}

/**
 * Remove all consecutive chars c from the beginning of the string
 * @param c Char to search for
 * @return The string stripped from the starting chars.
 */
fun String.cldRemoveStartingChars(c: Char): String {
    var lastToRemove = -1
    for (i in indices) {
        if (this[i] == c) {
            lastToRemove = i
            continue
        }
        if (this[i] != c) {
            break
        }
    }
    return if (lastToRemove < 0) this else substring(lastToRemove + 1)
}

public fun String.decodeURLPart(
    start: Int = 0,
    end: Int = length,
): String = decodeScan(start, end, false)

private fun String.decodeScan(start: Int, end: Int, plusIsSpace: Boolean): String {
    for (index in start until end) {
        val ch = this[index]
        if (ch == '%' || (plusIsSpace && ch == '+')) {
            return decodeImpl(start, end, index, plusIsSpace)
        }
    }
    return if (start == 0 && end == length) toString() else substring(start, end)
}

private fun CharSequence.decodeImpl(
    start: Int,
    end: Int,
    prefixEnd: Int,
    plusIsSpace: Boolean,
): String {
    val length = end - start
    // if length is big, it probably means it is encoded
    val sbSize = if (length > 255) length / 3 else length
    val sb = StringBuilder(sbSize)

    if (prefixEnd > start) {
        sb.append(this, start, prefixEnd)
    }

    var index = prefixEnd

    // reuse ByteArray for hex decoding stripes
    var bytes: ByteArray? = null

    while (index < end) {
        val c = this[index]
        when {
            plusIsSpace && c == '+' -> {
                sb.append(' ')
                index++
            }

            c == '%' -> {
                // if ByteArray was not needed before, create it with an estimate of remaining string be all hex
                if (bytes == null) {
                    bytes = ByteArray((end - index) / 3)
                }

                // fill ByteArray with all the bytes, so Charset can decode text
                var count = 0
                while (index < end && this[index] == '%') {
                    if (index + 2 >= end) {
                        throw URLDecodeException(
                            "Incomplete trailing HEX escape: ${substring(index)}, in $this at $index"
                        )
                    }

                    val digit1 = charToHexDigit(this[index + 1])
                    val digit2 = charToHexDigit(this[index + 2])
                    if (digit1 == -1 || digit2 == -1) {
                        throw URLDecodeException(
                            "Wrong HEX escape: %${this[index + 1]}${this[index + 2]}, in $this, at $index"
                        )
                    }

                    bytes[count++] = (digit1 * 16 + digit2).toByte()
                    index += 3
                }

                // Decode chars from bytes and put into StringBuilder
                // Note: Tried using ByteBuffer and using enc.decode() – it's slower
                sb.append(bytes.decodeToString(0, count))
            }

            else -> {
                sb.append(c)
                index++
            }
        }
    }

    return sb.toString()
}

public class URLDecodeException(message: String) : Exception(message)

private fun charToHexDigit(c2: Char) = when (c2) {
    in '0'..'9' -> c2 - '0'
    in 'A'..'F' -> c2 - 'A' + 10
    in 'a'..'f' -> c2 - 'a' + 10
    else -> -1
}
