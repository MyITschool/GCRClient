package com.elseboot3909.gcrclient.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.elseboot3909.gcrclient.R

@Composable
fun SetBackground() {
    Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)) {
        Image(
            painter = painterResource(id = R.drawable.ic_login_background),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun getBackgroundColor(): Color {
    return TopAppBarDefaults.smallTopAppBarColors().containerColor(
        scrollFraction = 1.0f
    ).value
}