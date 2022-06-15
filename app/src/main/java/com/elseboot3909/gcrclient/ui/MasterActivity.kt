package com.elseboot3909.gcrclient.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.elseboot3909.gcrclient.credentials.dataStores
import com.elseboot3909.gcrclient.remote.client
import com.elseboot3909.gcrclient.repository.repos
import com.elseboot3909.gcrclient.ui.change.ChangeScreenContent
import com.elseboot3909.gcrclient.ui.comment.CommentScreenContent
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.diff.DiffScreenContent
import com.elseboot3909.gcrclient.ui.home.HomeScreenContent
import com.elseboot3909.gcrclient.ui.login.LoginScreenContent
import com.elseboot3909.gcrclient.ui.search.SearchScreenContent
import com.elseboot3909.gcrclient.ui.switcher.SwitcherScreenContent
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.ui.vote.VoteScreenContent
import com.elseboot3909.gcrclient.viewmodel.credentials.CredentialsViewModel
import com.elseboot3909.gcrclient.viewmodel.viewModels
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
class MasterActivity : AppCompatActivity() {

    private val credentialsViewModel by lazy {
        getViewModel<CredentialsViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(applicationContext)
            modules(ArrayList<Module>().also {
                it.add(dataStores)
                it.add(viewModels)
                it.add(repos)
                it.add(client)
            })
        }

        credentialsViewModel.selected.observe(this) {
            credentialsViewModel.serversList.observe(this) {
                if (credentialsViewModel.isInitialized.value == false) {
                    credentialsViewModel.isInitialized.postValue(true)
                }
            }
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                credentialsViewModel.isInitialized.value == false
            }
            setOnExitAnimationListener { view ->
                val alpha = ObjectAnimator.ofFloat(
                    view.view,
                    View.ALPHA,
                    1f,
                    0f
                )
                alpha.duration = 750L
                alpha.doOnEnd { view.remove() }
                alpha.start()
            }
        }

        credentialsViewModel.isInitialized.observe(this) {
            if (it) {
                setContent {
                    MainTheme {
                        MasterNavCtl()
                    }
                }
            }
        }
    }

    @Composable
    private fun MasterNavCtl() {
        val navController = rememberAnimatedNavController()
        val systemUiController = rememberSystemUiController()
        val serversListSize = credentialsViewModel.serversList.value?.serverDataList?.size ?: 0
        AnimatedNavHost(
            navController = navController,
            startDestination = if (serversListSize == 0) "${MasterScreens.LoginScreen.route}/{isInit}" else MasterScreens.HomeScreen.route
        ) {
            composable(
                route = "${MasterScreens.LoginScreen.route}/{isInit}",
                arguments = listOf(navArgument(name = "isInit") {
                    type = NavType.BoolType
                    defaultValue = serversListSize == 0
                }),
                enterTransition = {
                    when (initialState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                popExitTransition = {
                    when (targetState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                exitTransition = {
                    when (initialState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                }
            ) {
                systemUiController.setSystemBarsColor(MaterialTheme.colorScheme.surface)
                LoginScreenContent(it.arguments?.getBoolean("isInit") ?: true, navController)
            }
            composable(
                route = MasterScreens.HomeScreen.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        "${MasterScreens.LoginScreen.route}/{isInit}",
                        MasterScreens.SwitcherScreen.route -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        "${MasterScreens.LoginScreen.route}/{isInit}",
                        MasterScreens.SwitcherScreen.route,
                        MasterScreens.ChangeScreen.route -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        "${MasterScreens.LoginScreen.route}/{isInit}",
                        MasterScreens.SwitcherScreen.route,
                        MasterScreens.ChangeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                }
            ) {
                systemUiController.setNavigationBarColor(getBackgroundColor())
                HomeScreenContent(navController)
            }
            composable(
                route = MasterScreens.SearchScreen.route
            ) {
                systemUiController.setStatusBarColor(getBackgroundColor())
                systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.surface)
                SearchScreenContent(navController)
            }
            composable(
                route = MasterScreens.SwitcherScreen.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                popExitTransition = {
                    when (targetState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                }
            ) {
                systemUiController.setSystemBarsColor(MaterialTheme.colorScheme.surface)
                SwitcherScreenContent(navController)
            }
            composable(
                route = MasterScreens.ChangeScreen.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        "${MasterScreens.VoteScreen.route}/{label}" -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        MasterScreens.HomeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                        }
                        "${MasterScreens.VoteScreen.route}/{label}" -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                }
            ) {
                ChangeScreenContent(navController)
                systemUiController.setSystemBarsColor(getBackgroundColor())
            }
            composable(
                route = "${MasterScreens.VoteScreen.route}/{label}",
                arguments = listOf(
                    navArgument(name = "label") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                ),
                enterTransition = {
                    when (initialState.destination.route) {
                        MasterScreens.ChangeScreen.route -> {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                        }
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        MasterScreens.ChangeScreen.route -> {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                        }
                        else -> null
                    }
                }
            ) {
                systemUiController.setStatusBarColor(getBackgroundColor())
                systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.surface)
                VoteScreenContent(navController, it.arguments?.getString("label") ?: "")
            }
            composable(
                route = MasterScreens.DiffScreen.route
            ) {
                systemUiController.setStatusBarColor(getBackgroundColor())
                systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.surface)
                DiffScreenContent(navController)
            }
            composable(
                route = MasterScreens.CommentScreen.route
            ) {
                systemUiController.setStatusBarColor(getBackgroundColor())
                systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.surface)
                CommentScreenContent(navController)
            }
        }
    }

    fun restartRequest() {
        Toast.makeText(this, "Restart the application", Toast.LENGTH_LONG).show()
        this.finishAffinity()
    }

}