package com.elseboot3909.gcrclient.ui.search

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elseboot3909.gcrclient.ui.search.screens.MainSearch
import com.elseboot3909.gcrclient.ui.search.screens.ParamsList
import com.elseboot3909.gcrclient.ui.search.screens.ProjectsListContent
import com.elseboot3909.gcrclient.ui.search.screens.UsersListContent

@Composable
fun SearchScreenContent(masterNavCtl: NavHostController) {
    SearchScreenNavCtl(masterNavCtl)
}

@Composable
private fun SearchScreenNavCtl(masterNavCtl: NavHostController) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.MainSearch.route) {
        composable(route = Screens.MainSearch.route) {
            MainSearch(navController, masterNavCtl)
        }
        composable(route = Screens.ProjectsList.route) {
            ProjectsListContent(navController)
        }
        composable(route = Screens.UsersList.route) {
            UsersListContent(navController)
        }
        composable(route = Screens.ParamsList.route) {
            ParamsList(navController)
        }
    }
}