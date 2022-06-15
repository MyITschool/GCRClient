@file:OptIn(ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.login

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.elseboot3909.gcrclient.ui.common.SetBackground
import com.elseboot3909.gcrclient.ui.login.screens.CredentialsInput
import com.elseboot3909.gcrclient.ui.login.screens.HelloApp
import com.elseboot3909.gcrclient.ui.login.screens.ServerInput
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@Composable
internal fun LoginScreenContent(isInit: Boolean, masterNavCtl: NavHostController) {
    SetBackground()
    NavCtl(isInit, masterNavCtl)
}

@Composable
private fun NavCtl(isInit: Boolean, masterNavCtl: NavHostController) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = if (isInit) Screens.HelloApp.route else Screens.ServerInput.route
    ) {
        composable(
            route = Screens.HelloApp.route,
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
            }
        ) {
            HelloApp(navController = navController)
        }
        composable(
            route = Screens.ServerInput.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
            }
        ) {
            ServerInput(navController = navController)
        }
        composable(
            route = "${Screens.Login.route}/{serverURL}",
            arguments = listOf(navArgument(name = "serverURL") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
            }
        ) {
            CredentialsInput(it.arguments?.getString("serverURL") ?: "", isInit, masterNavCtl)
        }
    }
}