@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.search.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.search.Screens
import com.elseboot3909.gcrclient.repository.SearchParamsRepository
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.SearchParamsViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun MainSearch(
    navController: NavHostController,
    masterNavCtl: NavHostController
) {
    MainSearchTopAppBar(navController, masterNavCtl)
}

@Composable
private fun MainSearchTopAppBar(
    navController: NavHostController,
    masterNavCtl: NavHostController,
    screenWidth: Int = LocalConfiguration.current.screenWidthDp,
    spRepo: SearchParamsRepository = get()
) {
    var searchStr by remember { mutableStateOf(spRepo.searchString.value) }
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = getBackgroundColor()) {
                val focusRequester = remember { FocusRequester() }
                BasicTextField(
                    value = searchStr,
                    onValueChange = {
                        spRepo.searchString.value = it
                        searchStr = it
                    },
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Search") },
                icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                onClick = {
                    spRepo.selectedProjects = ArrayList(spRepo.preSelectedProjects)
                    spRepo.selectedUsers = ArrayList(spRepo.preSelectedUsers)
                    spRepo.oldSearchString.value = spRepo.searchString.value
                    spRepo.buildQueryString()
                    masterNavCtl.popBackStack()
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            MainSearchContent(navController, screenWidth)
        }
    }
    BackHandler(true) {
        spRepo.preSelectedProjects = ArrayList(spRepo.selectedProjects)
        spRepo.preSelectedUsers = ArrayList(spRepo.selectedUsers)
        spRepo.searchString.value = spRepo.oldSearchString.value
        spRepo.selectedUsersCounter.value = spRepo.preSelectedUsers.size
        spRepo.selectedProjectsCounter.value = spRepo.preSelectedProjects.size
        masterNavCtl.popBackStack()
    }
}

@Composable
private fun MainSearchContent(
    navController: NavHostController,
    screenWidth: Int,
    spRepo: SearchParamsRepository = get()
) {
    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 42.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Projects", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .padding(start = (screenWidth * 0.1).dp)
                    .border(
                        width = ButtonDefaults.OutlinedBorderSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ButtonDefaults.OutlinedBorderOpacity),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { navController.navigate(route = Screens.ProjectsList.route) }
                    .padding(start = 12.dp, end = 12.dp)
                    .defaultMinSize(minHeight = 32.dp)
                    .wrapContentSize()
            ) {
                val counter = spRepo.selectedProjectsCounter.collectAsState(0)
                Text(
                    text = counter.value.let { if (it == 0) "Select project" else "$it selected" },
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 42.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Users", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .padding(start = (screenWidth * 0.1).dp)
                    .border(
                        width = ButtonDefaults.OutlinedBorderSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ButtonDefaults.OutlinedBorderOpacity),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { navController.navigate(route = Screens.UsersList.route) }
                    .padding(start = 12.dp, end = 12.dp)
                    .defaultMinSize(minHeight = 32.dp)
                    .wrapContentSize()
            ) {
                val counter = spRepo.selectedUsersCounter.collectAsState(0)
                Text(
                    text = counter.value.let { if (it == 0) "Select user" else "$it selected" },
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}