package com.cloudinary.transformation.animated

import com.cloudinary.transformation.*

class AnimatedEdit(
    private val delay: Int? = null,
    private val loop: Int? = null
): Animated() {
    override fun toString(): String {
        return listOfNotNull(delay?.let { "dl_$it" }, loop?.let { "e_loop:$it" }).joinToString(",")
    }

    class Builder internal constructor() :
        AnimatedComponentBuilder {

        private var delay: Int? = null
        private var loop: Int? = null

        fun delay(delay: Int) = apply { this.delay = delay }
        fun loop(loop: Int) = apply { this.loop = loop }

        override fun build() = AnimatedEdit(delay, loop)
    }
}