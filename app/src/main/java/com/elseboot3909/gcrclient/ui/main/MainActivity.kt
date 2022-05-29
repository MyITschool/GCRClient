package com.elseboot3909.gcrclient.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.ui.account.SwitchServer
import com.elseboot3909.gcrclient.ui.login.LoginActivity
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.ui.theme.NoRippleTheme
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.ServerDataManager
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {

    private val searchModel: SearchParamsViewModel by viewModels()
    private val changesModel: ChangesViewModel by viewModels()
    private val starredModel: StarredViewModel by viewModels()

    val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Constants.ACCOUNT_SWITCHED -> reloadActivity()
            Constants.SEARCH_ACQUIRED -> {
                searchModel.setQuery(
                    result.data?.getStringExtra(Constants.SEARCH_STRING_KEY) ?: "",
                    result.data?.getStringArrayListExtra(Constants.SEARCH_PROJECTS_KEY)
                        ?: ArrayList()
                )
            }
            Constants.CHANGE_STATE_CHANGED -> {
                val query = searchModel.getQuery().value
                if (query != null) {
                    changesModel.getChangesList(query, 0)
                    starredModel.getStarredList()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ServerDataManager.serverDataList.isEmpty()) {
            activityResultLauncher.launch(Intent(this, LoginActivity::class.java))
        } else {
            setContent {
                MainTheme {
                    NavDrawer()
                }
            }
        }
    }

    @Composable
    private fun NavCtl(drawerState: DrawerState) {
        val navController = rememberAnimatedNavController()
        Scaffold(
            bottomBar = {
                val screensList =
                    listOf(Screens.Changes, Screens.Starred, Screens.Profile)
                var selectedItem by remember { mutableStateOf(0) }
                CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                    NavigationBar {
                        screensList.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selectedItem == index) item.iconPressed else item.icon,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = item.title) },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    navController.navigate(item.route)
                                },
                                modifier = Modifier.clickable { }
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedNavHost(
                    navController = navController,
                    startDestination = Screens.Changes.route
                ) {
                    composable(
                        route = Screens.Changes.route,
                        enterTransition = {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
                        },
                        exitTransition = {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
                        }) {
                        Changes(drawerState)
                    }
                    composable(route = Screens.Starred.route,
                        enterTransition = {
                            when (initialState.destination.route) {
                                Screens.Changes.route -> {
                                    slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
                                }
                                else -> {
                                    slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
                                }
                            }
                        },
                        exitTransition = {
                            when (initialState.destination.route) {
                                Screens.Changes.route -> {
                                    slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
                                }
                                else -> {
                                    slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
                                }
                            }
                        }) {
                        Starred(drawerState)
                    }
                    composable(
                        route = Screens.Profile.route,
                        enterTransition = {
                            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(175))
                        },
                        exitTransition = {
                            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(175))
                        }) {
                        Profile(drawerState)
                    }
                }
            }
        }
    }

    private fun reloadActivity() {
        finish()
        startActivity(intent)
    }

    @ExperimentalAnimationApi
    @ExperimentalMaterial3Api
    @Composable
    fun NavDrawer() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val materialAlertDialogExit = MaterialAlertDialogBuilder(context)
        materialAlertDialogExit.setTitle("Are you sure you want to log out?")
        materialAlertDialogExit.setNeutralButton("Cancel", null)
        materialAlertDialogExit.setPositiveButton("Yes") { _, _ ->
            ServerDataManager.serverDataList.removeAt(ServerDataManager.selectedPos)
            ServerDataManager.writeServerDataList(context)
            ServerDataManager.writeNewPosition(context, 0)
            (context as MainActivity).reloadActivity()
        }
        val drawerItems = listOf(
            DrawerOption(title = "Switch Server", icon = Icons.Default.MenuOpen, onClick = {
                scope.launch {
                    delay(Constants.NAV_DRAWER_TIMEOUT)
                    if (ServerDataManager.serverDataList.size > 1) {
                        (context as MainActivity).activityResultLauncher.launch(
                            Intent(context, SwitchServer::class.java)
                        )
                    } else {
                        Toast.makeText(context, "Nothing to choose from", Toast.LENGTH_SHORT).show()
                    }
                }
            }),
            DrawerOption(title = "Add new server", icon = Icons.Default.Add, onClick = {
                scope.launch {
                    delay(Constants.NAV_DRAWER_TIMEOUT)
                    (context as MainActivity).activityResultLauncher.launch(
                        Intent(context, LoginActivity::class.java)
                    )
                }
            }),
            DrawerOption(title = "Log out from server", icon = Icons.Default.Logout, onClick = {
                scope.launch {
                    delay(Constants.NAV_DRAWER_TIMEOUT)
                    materialAlertDialogExit.show()
                }
            })
        )
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column {
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
                            Text(text = "GCRClient", style = MaterialTheme.typography.headlineLarge)
                        }
                        Text(
                            text = "Selected server:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (ServerDataManager.serverDataList.isNotEmpty()) ServerDataManager.serverDataList[ServerDataManager.selectedPos].toString() else "No server",
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = "Manage servers",
                        modifier = Modifier.padding(start = 26.dp, bottom = 2.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    drawerItems.forEach { item ->
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
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "", modifier = Modifier.padding(end = screenWidth.dp))
                        }
                    }
                }
            },
            content = { NavCtl(drawerState) },
            scrimColor = Color.Transparent
        )
    }

    data class DrawerOption(val title: String, val icon: ImageVector, val onClick: () -> Unit)
}