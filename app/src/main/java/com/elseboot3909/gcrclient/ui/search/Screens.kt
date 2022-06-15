package com.elseboot3909.gcrclient.ui.search

internal sealed class Screens(val route: String) {
    object MainSearch : Screens("main_search_screen")
    object ProjectsList : Screens("project_list_screen")
    object ParamsList : Screens("params_list_screen")
    object UsersList : Screens("users_list_screen")
}