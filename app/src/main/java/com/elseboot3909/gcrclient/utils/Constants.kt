@file:OptIn(ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.utils

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize

object Constants {
    const val LOG_TAG = "GCRClient"

    const val SECURITY_KEY = "gcrclient"

    const val NAV_DRAWER_TIMEOUT = 250L



    const val MAX_FETCHED_CHANGES = 25
}

object Animations {
    const val SCREENS_ANIM_TIME = 250
    const val SPLASH_ANIM_TIME = 750L
}

