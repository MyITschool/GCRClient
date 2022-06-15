@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.vote

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.common.progress.ProgressBar
import com.elseboot3909.gcrclient.ui.vote.screens.VoteActions
import com.elseboot3909.gcrclient.ui.vote.screens.VoteExtended
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@Composable
internal fun VoteScreenContent(
    masterNavCtl: NavController,
    label: String
) {
    VoteScreenTopBar(masterNavCtl, label)
}

@Composable
private fun VoteScreenTopBar(
    masterNavCtl: NavController,
    label: String
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.wrapContentHeight()) {
                TopAppBar(
                    title = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            masterNavCtl.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    backgroundColor = getBackgroundColor()
                )
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            VoteScreenNavCtl(label)
            ProgressBar()
        }
    }
}

@Composable
private fun VoteScreenNavCtl(label: String) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = Screens.VoteExtended.route) {
        composable(
            route = Screens.VoteExtended.route,
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
            }
        ) {
            VoteExtended(label, navController)
        }
        composable(
            route = Screens.VoteActions.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
            }
        ) {
            VoteActions(label, navController)
        }
    }
}