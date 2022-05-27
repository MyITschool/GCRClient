package com.elseboot3909.gcrclient.ui.change

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.api.AccountAPI
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.ui.theme.NoRippleTheme
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class ChangeActivity : AppCompatActivity() {

    private val changeInfo: ChangeInfo by lazy {
        intent.extras?.getSerializable("changeInfo") as ChangeInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)

        setContent {
            MainTheme {
                NavCtl()
            }
        }
    }

    @Composable
    fun NavCtl() {
        val navController = rememberAnimatedNavController()
        val backgroundColor =
            if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            val statusList = ArrayList<String>()
                            if (changeInfo.work_in_progress) statusList.add(getString(R.string.wip))
                            when (changeInfo.status) {
                                "NEW" -> statusList.add(getString(R.string.active))
                                "MERGED" -> statusList.add(getString(R.string.active))
                                "ABANDONED" -> statusList.add(getString(R.string.abandoned))
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
                            var isStarred by remember { mutableStateOf(changeInfo.starred) }
                            IconButton(onClick = {
                                val retrofit = NetManager.getRetrofitConfiguration(null, true)

                                val accountAPI = retrofit.create(AccountAPI::class.java)
                                val request: Call<String> =
                                    if (isStarred) accountAPI.removeStarredChange(changeInfo.id) else accountAPI.putStarredChange(
                                        changeInfo.id
                                    )

                                request.enqueue(object : Callback<String> {
                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        if (response.isSuccessful) isStarred = !isStarred
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                    }

                                })
                            }) {
                                Icon(
                                    imageVector = if (isStarred) Icons.Filled.Star else Icons.TwoTone.Star,
                                    contentDescription = "Set starred"
                                )
                            }
                        },
                        backgroundColor = backgroundColor
                    )
                    Text(
                        text = changeInfo.subject,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = backgroundColor)
                            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                    )
                }
            },
            bottomBar = {
                val screensList =
                    listOf(Screens.Info, Screens.Code, Screens.Vote, Screens.Comment)
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
                AnimatedNavHost(
                    navController = navController,
                    startDestination = Screens.Info.route
                ) {
                    composable(route = Screens.Info.route, popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(175)
                        )
                    }, enterTransition = {
                        if (route == Screens.Code.route) {
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(175)
                            )
                        } else {
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(175)
                            )
                        }
                    }, exitTransition = {
                        if (route == Screens.Code.route) {
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(175)
                            )
                        } else {
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(175)
                            )
                        }
                    }) {
                        Info(changeInfo)
                    }
                    composable(route = Screens.Code.route) {
                        Code(changeInfo)
                    }
                    composable(route = Screens.Vote.route) {
                        Vote(changeInfo)
                    }
                    composable(route = Screens.Comment.route) {
                        Comment()
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }

}