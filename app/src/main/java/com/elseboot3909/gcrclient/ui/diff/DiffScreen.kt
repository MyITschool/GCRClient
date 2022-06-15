@file:OptIn(ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.diff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.diff.screens.CompareLines

@Composable
internal fun DiffScreenContent(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                backgroundColor = getBackgroundColor()
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            DiffScreen()
        }
    }
}

@Composable
private fun DiffScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.CompareLines.route) {
        composable(route = Screens.CompareLines.route) {
            CompareLines()
        }
    }
}

