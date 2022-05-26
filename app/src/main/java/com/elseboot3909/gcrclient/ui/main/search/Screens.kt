package com.elseboot3909.gcrclient.ui.main.search

sealed class Screens(val route: String) {
    object MainSearch : Screens("main_search_screen")
    object ProjectsList : Screens("project_list_screen")
    object GroupsList : Screens("groups_list_screen")
    object MembersList : Screens("members_list_screen")
}