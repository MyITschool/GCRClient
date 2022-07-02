@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.credentials.dataStore.CredentialsDataStore
import com.elseboot3909.gcrclient.credentials.dataStore.SelectedDataStore
import com.elseboot3909.gcrclient.entity.internal.DrawerOption
import com.elseboot3909.gcrclient.repository.CredentialsRepository
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.home.screens.Changes
import com.elseboot3909.gcrclient.ui.home.screens.Starred
import com.elseboot3909.gcrclient.ui.theme.NoRippleTheme
import com.elseboot3909.gcrclient.utils.Animations.SCREEN_ANIMATION
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.CredentialsViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun HomeScreenContent(masterNavCtl: NavHostController) {
    HomeScreenNavDrawer(masterNavCtl)
}

@Composable
private fun HomeScreenNavDrawer(
    masterNavCtl: NavHostController,
    cViewModel: CredentialsViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier.padding(
                    start = 26.dp,
                    top = 24.dp,
                    bottom = 28.dp
                )
            ) {
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    Image(
                        painter = painterResource(R.drawable.ic_nav_view_icon),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
                }
                Text(
                    text = stringResource(R.string.selected_server),
                    style = MaterialTheme.typography.bodyLarge
                )
                val currentSD = cViewModel.currentServerData.observeAsState(ServerData.getDefaultInstance())
                Text(
                    text = currentSD.value.serverURL.also { it.ifEmpty { "No server" } },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(R.string.manage_servers),
                modifier = Modifier.padding(start = 26.dp, bottom = 2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            getDrawerOptionList(
                scope = scope,
                context = context,
                masterNavCtl = masterNavCtl
            ).forEach { item ->
                TextButton(
                    onClick = {
                        scope.launch { drawerState.close() }
                        item.onClick()
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(item.title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "", modifier = Modifier.padding(end = screenWidth.dp))
                }
            }
        },
        scrimColor = Color.Transparent,
        content = {
            HomeScreenNavCtl(drawerState, masterNavCtl)
        }
    )
}

private fun buildExitAlertDialog(context: Context): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(context).also {
        it.setTitle(R.string.log_out_question)
        it.setNeutralButton(R.string.cancel, null)
        it.setPositiveButton(R.string.yes) { _, _ ->
            (context as MasterActivity).let { ctx ->
                ctx.get<SelectedDataStore>().updateSelected(0)
                ctx.get<CredentialsDataStore>().removeServerData(ctx.get<ServerData>().serverURL)
                if (ctx.get<CredentialsRepository>().serversList.value.isEmpty()) {
                    ctx.finishAffinity()
                } else {
                    ctx.resetData()
                }
            }
        }
    }
}


private fun getDrawerOptionList(
    scope: CoroutineScope,
    context: Context,
    masterNavCtl: NavHostController
): List<DrawerOption> {
    return listOf(
        DrawerOption(
            title = R.string.switch_server,
            icon = Icons.Default.MenuOpen,
            onClick = {
                scope.launch {
                    delay(Constants.NAV_DRAWER_TIMEOUT)
                    masterNavCtl.navigate(route = MasterScreens.SwitcherScreen.route)
                }
            }),
        DrawerOption(
            title = R.string.add_server,
            icon = Icons.Default.Add,
            onClick = {
                scope.launch {
                    delay(Constants.NAV_DRAWER_TIMEOUT)
                    masterNavCtl.navigate(route = "${MasterScreens.LoginScreen.route}/${false}")
                }
            }),
        DrawerOption(
            title = R.string.log_out,
            icon = Icons.Default.Logout,
            onClick = {
                scope.launch {
                    delay(Constants.NAV_DRAWER_TIMEOUT)
                    val exitAlertDialog = buildExitAlertDialog(context)
                    exitAlertDialog.show()
                }
            })
    )
}

@Composable
private fun HomeScreenNavCtl(drawerState: DrawerState, masterNavCtl: NavHostController) {
    val navController = rememberAnimatedNavController()
    val systemUiController = rememberSystemUiController()
    Scaffold(
        bottomBar = {
            val screensList = Screens::class.sealedSubclasses.mapNotNull { it.objectInstance }
            CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                NavigationBar {
                    val currentBackStack by navController.currentBackStackEntryAsState()
                    var current = currentBackStack?.destination?.route
                    screensList.forEach{ item ->
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
                                navController.navigate(item.route)
                            },
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AnimatedNavHost(navController = navController, startDestination = Screens.Changes.route) {
                composable(
                    route = Screens.Changes.route,
                    enterTransition = {
                        when (initialState.destination.route) {
                            Screens.Starred.route,
//                            Screens.Profile.route -> {
                            -> {
                                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, SCREEN_ANIMATION)
                            }
                            else -> null
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            Screens.Starred.route,
//                            Screens.Profile.route -> {
                            -> {
                                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, SCREEN_ANIMATION)
                            }
                            else -> null
                        }
                    }
                ) {
                    Changes(drawerState, masterNavCtl)
                    systemUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)
                }
                composable(
                    route = Screens.Starred.route,
                    enterTransition = {
                        when (initialState.destination.route) {
                            Screens.Changes.route -> {
                                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, SCREEN_ANIMATION)
                            }
//                            Screens.Profile.route -> {
//                                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, SCREEN_ANIMATION)
//                            }
                            else -> null
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            Screens.Changes.route -> {
                                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, SCREEN_ANIMATION)
                            }
//                            Screens.Profile.route -> {
//                                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, SCREEN_ANIMATION)
//                            }
                            else -> null
                        }
                    }
                ) {
                    systemUiController.setStatusBarColor(getBackgroundColor())
                    Starred(drawerState, masterNavCtl)
                }
//                composable(
//                    route = Screens.Profile.route,
//                    enterTransition = {
//                        when (initialState.destination.route) {
//                            Screens.Changes.route,
//                            Screens.Starred.route -> {
//                                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, SCREEN_ANIMATION)
//                            }
//                            else -> null
//                        }
//                    },
//                    exitTransition = {
//                        when (targetState.destination.route) {
//                            Screens.Changes.route,
//                            Screens.Starred.route -> {
//                                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, SCREEN_ANIMATION)
//                            }
//                            else -> null
//                        }
//                    }
//                ) {
//                    systemUiController.setStatusBarColor(getBackgroundColor())
//                    Profile(drawerState)
//                }
            }
        }
    }
}

private fun getCurrentRoute(navController: NavController): String {
    return navController.currentBackStackEntry?.destination?.route ?: ""
}
