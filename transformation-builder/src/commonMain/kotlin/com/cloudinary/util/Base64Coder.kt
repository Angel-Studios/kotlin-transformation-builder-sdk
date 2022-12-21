//Copyright 2003-2010 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland
//www.source-code.biz, www.inventec.ch/chdh
//
//This module is multi-licensed and may be used under the terms
//of any of the following licenses:
//
//EPL, Eclipse Public License, V1.0 or later, http://www.eclipse.org/legal
//LGPL, GNU Lesser General Public License, V2.1 or later, http://www.gnu.org/licenses/lgpl.html
//GPL, GNU General Public License, V2 or later, http://www.gnu.org/licenses/gpl.html
//AGPL, GNU Affero General Public License V3 or later, http://www.gnu.org/licenses/agpl.html
//AL, Apache License, V2.0 or later, http://www.apache.org/licenses
//BSD, BSD License, http://www.opensource.org/licenses/bsd-license.php
//MIT, MIT License, http://www.opensource.org/licenses/MIT
//
//Please contact the author if you need another license.
//This module is provided "as is", without warranties of any kind.
package com.cloudinary.util

import kotlin.jvm.JvmOverloads

/**
 * A Base64 encoder/decoder.
 *
 *
 *
 *
 * This class is used to encode and decode data in Base64 format as described in
 * RFC 1521.
 *
 * @author Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland,
 * www.source-code.biz
 */
object Base64Coder {
    // Mapping table from 6-bit nibbles to Base64 characters.
    private val map1 = ('A'..'Z')
        .plus('a'..'z')
        .plus('0'..'9')
        .plus('+')
        .plus('/')
        .toCharArray()

    /**
     * Encodes a string into Base64 format. No blanks or line breaks are
     * inserted.
     *
     * @param input A String to be encoded.
     * @return A String containing the Base64 encoded data.
     */
    fun encodeString(input: String): String {
        return encode(input.encodeToByteArray()).toString()
    }
    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are
     * inserted in the output.
     *
     * @param input   An array containing the data bytes to be encoded.
     * @param iOff Offset of the first byte in `in` to be processed.
     * @param iLen Number of bytes to process in `in`, starting at
     * `iOff`.
     * @return A character array containing the Base64 encoded data.
     */
    @JvmOverloads
    fun encode(input: ByteArray, iOff: Int = 0, iLen: Int = input.size): CharArray {
        val oDataLen = (iLen * 4 + 2) / 3 // output length without padding
        val oLen = (iLen + 2) / 3 * 4 // output length including padding
        val out = CharArray(oLen)
        var ip = iOff
        val iEnd = iOff + iLen
        var op = 0
        while (ip < iEnd) {
            val i0 = input[ip++].toInt() and 0xff
            val i1 = if (ip < iEnd) input[ip++].toInt() and 0xff else 0
            val i2 = if (ip < iEnd) input[ip++].toInt() and 0xff else 0
            val o0 = i0 ushr 2
            val o1 = i0 and 3 shl 4 or (i1 ushr 4)
            val o2 = i1 and 0xf shl 2 or (i2 ushr 6)
            val o3 = i2 and 0x3F
            out[op++] = map1[o0]
            out[op++] = map1[o1]
            out[op] = if (op < oDataLen) map1[o2] else '='
            op++
            out[op] = if (op < oDataLen) map1[o3] else '='
            op++
        }
        return out
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are
     * inserted in the output.
     *
     * @param input` An array containing the data bytes to be encoded.
     * @return A character array containing the Base64 encoded data.
     */
    fun encodeURLSafeString(input: String): String {
        return encodeURLSafeString(input.encodeToByteArray())
    }

    fun encodeURLSafeString(digest: ByteArray): String {
        val encode = encode(digest)
        for (i in encode.indices) {
            if (encode[i] == '+') {
                encode[i] = '-'
            } else if (encode[i] == '/') {
                encode[i] = '_'
            }
        }
        return encode.toString()
    }
}