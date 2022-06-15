package com.elseboot3909.gcrclient.ui.home.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
internal fun Profile(drawerState: DrawerState) {
    Text(text = "Profile", modifier = Modifier.fillMaxSize())
}