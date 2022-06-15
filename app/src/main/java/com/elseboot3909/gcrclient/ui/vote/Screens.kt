package com.elseboot3909.gcrclient.ui.vote

internal sealed class Screens(val route: String) {
    object VoteExtended : Screens("vote_extended_screen")
    object VoteActions : Screens("vote_actions_screen")
}
