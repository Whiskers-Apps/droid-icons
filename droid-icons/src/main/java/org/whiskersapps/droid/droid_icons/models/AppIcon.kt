package org.whiskersapps.droid.droid_icons.models

import android.graphics.drawable.Drawable

data class AppIcon(
    val stock: Icon,
    val themed: Icon?
)

data class Icon(
    val drawable: Drawable,
    val adaptive: Adaptive?
) {
    val isAdaptive: Boolean
        get() = adaptive != null

    data class Adaptive(
        val background: Drawable?,
        val foreground: Drawable
    )
}