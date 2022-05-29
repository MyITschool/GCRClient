package com.elseboot3909.gcrclient.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.elseboot3909.gcrclient.R

@Composable
fun SetBackground() {
    Image(
        painter = painterResource(id = R.drawable.ic_login_background),
        contentDescription = "background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun getBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
}