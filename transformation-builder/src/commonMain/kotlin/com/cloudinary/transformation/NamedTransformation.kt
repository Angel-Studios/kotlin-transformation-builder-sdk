package com.cloudinary.transformation

class NamedTransformation private constructor(private val name: Any) : Action {
    companion object {
        fun name(name: String) = NamedTransformation(name)
    }

    override fun toString(): String {
        return "t_$name"
    }
}