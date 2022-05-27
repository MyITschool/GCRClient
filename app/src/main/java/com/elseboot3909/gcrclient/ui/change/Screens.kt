package com.elseboot3909.gcrclient.ui.change

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val iconPressed: ImageVector
) {
    object Info : Screens(
        route = "info_screen",
        title = "Info",
        icon = Icons.Outlined.Info,
        iconPressed = Icons.Filled.Info
    )
    object Code : Screens(
        route = "code_screen",
        title = "Code",
        icon = Icons.Outlined.Description,
        iconPressed = Icons.Filled.Description
    )
    object Vote : Screens(
        route = "vote_screen",
        title = "Vote",
        icon = Icons.Outlined.HowToVote,
        iconPressed = Icons.Filled.HowToVote
    )
    object Comment : Screens(
        route = "comment_screen",
        title = "Comment",
        icon = Icons.Outlined.Forum,
        iconPressed = Icons.Filled.Forum
    )
}

