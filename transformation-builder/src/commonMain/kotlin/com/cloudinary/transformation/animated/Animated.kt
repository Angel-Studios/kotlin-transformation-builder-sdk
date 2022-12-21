package com.cloudinary.transformation.animated

import com.cloudinary.transformation.Action
import com.cloudinary.transformation.AnimatedComponentBuilder


abstract class Animated: Action {
    companion object {
        fun edit(options: (AnimatedEdit.Builder.() -> Unit)? = null) =
            buildAnimated(AnimatedEdit.Builder(), options)
    }
}

private fun <T : AnimatedComponentBuilder> buildAnimated(builder: T, options: (T.() -> Unit)?): Animated {
    options?.let { builder.it() }
    return builder.build() as Animated
}