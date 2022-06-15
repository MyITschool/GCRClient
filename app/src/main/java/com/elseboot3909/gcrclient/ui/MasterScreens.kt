package com.elseboot3909.gcrclient.ui

sealed class MasterScreens(val route: String) {
    object HomeScreen : MasterScreens("home_screen")
    object LoginScreen : MasterScreens("login_screen")
    object ChangeScreen : MasterScreens("change_screen")
    object VoteScreen : MasterScreens("vote_screen")
    object SwitcherScreen : MasterScreens("switcher_screen")
    object SearchScreen : MasterScreens("search_screen")
    object DiffScreen : MasterScreens("diff_screen")
    object CommentScreen : MasterScreens("comment_screen")
}