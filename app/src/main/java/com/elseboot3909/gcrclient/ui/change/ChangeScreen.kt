@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.change

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.remote.api.AccountAPI
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.change.screens.Code
import com.elseboot3909.gcrclient.ui.change.screens.Comment
import com.elseboot3909.gcrclient.ui.change.screens.Info
import com.elseboot3909.gcrclient.ui.change.screens.Vote
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.common.progress.ProgressBar
import com.elseboot3909.gcrclient.ui.theme.NoRippleTheme
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
internal fun ChangeScreenContent(masterNavCtl: NavController) {
    ChangeScreenScaffold(masterNavCtl)
}

@Composable
private fun ChangeScreenScaffold(
    masterNavCtl: NavController,
    changeInfoRepo: ChangeInfoRepository = get(),
    progressBarRepository: ProgressBarRepository = get()
) {
    val navController = rememberAnimatedNavController()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as MasterActivity
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.wrapContentHeight()
            ) {
                val changeInfo: ChangeInfo by changeInfoRepo.changeInfo.collectAsState()
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { masterNavCtl.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        val statusList = ArrayList<String>()
                        if (changeInfo.work_in_progress) statusList.add(activity.getString(R.string.wip))
                        when (changeInfo.status) {
                            "NEW" -> statusList.add(activity.getString(R.string.active))
                            "MERGED" -> statusList.add(activity.getString(R.string.active))
                            "ABANDONED" -> statusList.add(activity.getString(R.string.abandoned))
                        }
                        for (status in statusList) {
                            Text(
                                text = status,
                                modifier = Modifier
                                    .padding(start = 2.dp, end = 2.dp)
                                    .border(
                                        width = 1.2.dp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .padding(
                                        bottom = 2.dp,
                                        top = 2.dp,
                                        start = 8.dp,
                                        end = 8.dp
                                    )
                            )
                        }
                        IconButton(onClick = {
                            scope.launch {
                                progressBarRepository.acquire()
                                val response: HttpResponse = if (changeInfo.starred) AccountAPI.removeDefaultStarFromChange(changeInfo) else AccountAPI.putDefaultStarOnChange(changeInfo)
                                if (response.status.value in 200..299) {
                                    changeInfoRepo.syncChangeWithRemote()
                                }
                                progressBarRepository.release()
                            }
                        }) {
                            Icon(
                                imageVector = if (changeInfo.starred) Icons.Filled.Star else Icons.TwoTone.Star,
                                contentDescription = "Set starred"
                            )
                        }
                    },
                    backgroundColor = getBackgroundColor()
                )
                Text(
                    text = changeInfo.subject,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = getBackgroundColor())
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                )
            }
        },
        bottomBar = {
            val screensList = Screens::class.sealedSubclasses.mapNotNull { it.objectInstance }
            val currentBackStack by navController.currentBackStackEntryAsState()
            var current = currentBackStack?.destination?.route
            CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                NavigationBar {
                    screensList.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (current == item.route) item.iconPressed else item.icon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = item.title) },
                            selected = current == item.route,
                            onClick = {
                                current = getCurrentRoute(navController)
                                navController.navigate(item.route) {
                                    if (item.route != Screens.Info.route) popUpTo(route = Screens.Info.route)
                                }
                            },
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            ChangeScreenNavCtl(navController, masterNavCtl)
            ProgressBar()
        }
    }
}

@Composable
private fun ChangeScreenNavCtl(navController: NavHostController, masterNavCtl: NavController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.Info.route
    ) {
        composable(
            route = Screens.Info.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Code.route,
                    Screens.Vote.route,
                    Screens.Comment.route -> {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                    }
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Code.route,
                    Screens.Vote.route,
                    Screens.Comment.route -> {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                    }
                    else -> null
                }
            }
        ) {
            Info()
        }
        composable(
            route = Screens.Code.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Info.route -> {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                    }
                    Screens.Comment.route,
                    Screens.Vote.route -> {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                    }
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Info.route -> {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                    }
                    Screens.Comment.route,
                    Screens.Vote.route -> {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                    }
                    else -> null
                }
            }
        ) {
            Code(masterNavCtl)
        }
        composable(
            route = Screens.Vote.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Info.route,
                    Screens.Code.route -> {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                    }
                    Screens.Comment.route -> {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                    }
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Info.route,
                    Screens.Code.route -> {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                    }
                    Screens.Comment.route -> {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                    }
                    else -> null
                }
            }
        ) {
            Vote(masterNavCtl)
        }
        composable(
            route = Screens.Comment.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Code.route,
                    Screens.Vote.route,
                    Screens.Info.route -> {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(250))
                    }
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Code.route,
                    Screens.Vote.route,
                    Screens.Info.route -> {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(250))
                    }
                    else -> null
                }
            }
        ) {
            Comment(masterNavCtl)
        }
    }
    BackHandler(true) {
        masterNavCtl.popBackStack()
    }
}

private fun getCurrentRoute(navController: NavController): String {
    return navController.currentBackStackEntry?.destination?.route ?: ""
}