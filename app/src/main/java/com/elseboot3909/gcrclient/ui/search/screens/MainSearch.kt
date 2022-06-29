@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.search.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.search.Screens
import com.elseboot3909.gcrclient.repository.SearchParamsRepository
import org.koin.androidx.compose.get

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
    searchRepo: SearchParamsRepository = get()
) {
    var searchStr by remember { mutableStateOf(searchRepo.searchString) }
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = getBackgroundColor()) {
                val focusRequester = remember { FocusRequester() }
                BasicTextField(
                    value = searchStr,
                    onValueChange = {
                        searchRepo.searchString = it
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
                    searchRepo.applySearchParams()
                    searchRepo.offset.value = 0
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
        searchRepo.restoreSearchParams()
        masterNavCtl.popBackStack()
    }
}

@Composable
private fun MainSearchContent(
    navController: NavHostController,
    screenWidth: Int,
    searchRepo: SearchParamsRepository = get()
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
                Text(
                    text = searchRepo.selectedProjectsCounter.value.let { if (it == 0) "Select project" else "$it selected" },
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
                Text(
                    text = searchRepo.selectedUsersCounter.value.let { if (it == 0) "Select user" else "$it selected" },
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}