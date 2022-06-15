package com.elseboot3909.gcrclient.ui.login

internal sealed class Screens(val route: String) {
    object HelloApp : Screens("hello_app_screen")
    object ServerInput : Screens("server_input_screen")
    object Login : Screens("login_screen")
}