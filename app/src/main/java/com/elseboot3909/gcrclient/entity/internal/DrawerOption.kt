package com.elseboot3909.gcrclient.entity.internal

import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerOption(
    val title: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)