package com.elseboot3909.gcrclient.ui.main.search

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.utils.Constants

@ExperimentalMaterial3Api
class SearchActivity : AppCompatActivity() {

    private val searchProjects: ArrayList<String> by lazy {
        ArrayList()
    }

    private val searchParams: SearchParams by lazy {
        SearchParams(strSearch = intent.getStringExtra(Constants.SEARCH_STRING_KEY) ?: "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainTheme {
                NavCtl()
            }
        }
    }

    @Composable
    private fun NavCtl() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.MainSearch.route) {
            composable(route = Screens.MainSearch.route) {
                MainSearch(navController, searchParams, searchProjects)
            }
            composable(route = Screens.ProjectsList.route) {
                ProjectsList(navController, searchProjects)
            }
            composable(route = Screens.ParamsList.route) {
                ParamsList(navController, searchProjects)
            }
        }
    }

}

data class SearchParams(val strSearch: String = "")