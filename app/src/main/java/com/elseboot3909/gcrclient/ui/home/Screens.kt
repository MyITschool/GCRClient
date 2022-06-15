package com.elseboot3909.gcrclient.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector

internal sealed class Screens(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val iconPressed: ImageVector
) {
    object Changes : Screens(
        route = "changes_screen",
        title = "Changes",
        icon = Icons.Outlined.Home,
        iconPressed = Icons.Filled.Home
    )
    object Starred : Screens(
        route = "starred_screen",
        title = "Starred",
        icon = Icons.Outlined.Grade,
        iconPressed = Icons.Outlined.Star
    )
//    object Profile : Screens(
//        route = "profile_screen",
//        title = "Profile",
//        icon = Icons.Outlined.Person,
//        iconPressed = Icons.Filled.Person
//    )
}