package com.cloudinary.transformation.expression

external fun cldNormalize(expression: String?): String

internal actual fun Any.cldNormalize(): String = cldNormalize(toString())
