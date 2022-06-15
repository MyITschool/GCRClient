package com.elseboot3909.gcrclient.entity.internal

import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerOption(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)