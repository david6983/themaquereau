package fr.isen.david.themaquereau.util

import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.ImageView

val fadeIn = AlphaAnimation(0f, 1f).apply {
    duration = 1000
    interpolator = AccelerateInterpolator()
}

val fadeOut = AlphaAnimation(1f, 0f).apply {
    duration = 1000
    interpolator = AccelerateInterpolator()
}