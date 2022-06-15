@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.login.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.login.Screens

@Composable
internal fun HelloApp(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .padding(start = 32.dp), verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome to GCRClient",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Row(
        Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        TextButton(
            onClick = {
                uriHandler.openUri("https://www.gerritcodereview.com")
            },
            Modifier.padding(start = 32.dp)
        ) {
            Text(
                text = "What is Gerrit?",
                style = MaterialTheme.typography.labelLarge
            )
        }
        OutlinedButton(
            onClick = { navController.navigate(route = Screens.ServerInput.route) },
            Modifier.padding(end = 32.dp)
        ) {
            Text(
                text = "Get started",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
    BackHandler(true) {
        (context as MasterActivity).finishAffinity()
    }
}