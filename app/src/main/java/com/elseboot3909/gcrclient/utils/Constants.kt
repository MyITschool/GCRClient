package com.elseboot3909.gcrclient.utils

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

object Constants {
    const val LOG_TAG = "GCRClient"
    const val NAV_DRAWER_TIMEOUT = 250L
    const val MAX_FETCHED_CHANGES = 25
}

object Animations {
    private const val SCREEN_ANIMATION_TIME = 250
    const val SPLASH_ANIMATION_TIME = 750L
    val SCREEN_ANIMATION: FiniteAnimationSpec<IntOffset> = tween(SCREEN_ANIMATION_TIME)
}

