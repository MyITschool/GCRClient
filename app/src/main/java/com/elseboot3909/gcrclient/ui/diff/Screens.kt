package com.elseboot3909.gcrclient.ui.diff

internal sealed class Screens(val route: String) {
    object CompareLines : Screens("compare_lines_screen")
    object CompareFiles : Screens("compare_files_screen")
}
