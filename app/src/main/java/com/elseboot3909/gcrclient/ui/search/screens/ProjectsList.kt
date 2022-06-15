@file:OptIn(ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.search.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.external.ProjectInfo
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.viewmodel.search.ProjectsListViewModel
import com.elseboot3909.gcrclient.repository.search.SearchParamsRepository
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun ProjectsListContent(navController: NavHostController) {
    ProjectsListTopAppBar(navController)
}

@Composable
private fun ProjectsListTopAppBar(
    navController: NavHostController,
    projectsModel: ProjectsListViewModel = getViewModel(),
    searchParamsRepo: SearchParamsRepository = get()
) {
    var searchStr by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = getBackgroundColor()) {
                val focusRequester = remember { FocusRequester() }
                BasicTextField(
                    value = searchStr,
                    onValueChange = { searchStr = it },
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
            val selectedCount = searchParamsRepo.selectedProjectsCounter.collectAsState()
            ExtendedFloatingActionButton(
                text = { Text(text = "Select ${selectedCount.value} " + if (selectedCount.value == 1) "project" else "projects") },
                icon = {
                    Icon(
                        imageVector = if (selectedCount.value <= 1) Icons.Default.Done else Icons.Default.DoneAll,
                        contentDescription = null
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 275))
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            ProjectsList( projectsModel, searchStr)
        }
    }
    BackHandler(true) {
        searchParamsRepo.restoreProjects()
        navController.popBackStack()
    }
}


@Composable
private fun ProjectsList(
    projectsModel: ProjectsListViewModel,
    searchStr: String
) {
    val projectsMap: HashMap<String, ProjectInfo> by projectsModel.projects.observeAsState(HashMap())
    val projectsList = projectsMap.keys.toList().sorted().map { n -> n }
        .filter { n -> n.contains(searchStr) || searchStr.isEmpty() }
    SwipeRefresh(
        state = rememberSwipeRefreshState(false),
        onRefresh = { projectsModel.refreshProjects() }) {
        LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
            items(
                count = projectsList.size,
                itemContent = {
                    ProjectsListItem(projectsMap[projectsList[it]] ?: ProjectInfo())
                }
            )
            item {
                Divider(
                    thickness = 80.dp,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun ProjectsListItem(
    projectInfo: ProjectInfo,
    searchParamsRepo: SearchParamsRepository = get()
) {
    val projectName = Uri.decode(projectInfo.id)
    var isSelected by remember { mutableStateOf(searchParamsRepo.selectedProjects.contains(projectName)) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                isSelected = !isSelected
                if (isSelected) {
                    searchParamsRepo.appendSelectedProjects(projectName)
                } else {
                    searchParamsRepo.removeSelectedProject(projectName)
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F7EA) else colorResource(R.color.neutral_1_100)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .defaultMinSize(minHeight = 32.dp)
                .padding(top = 4.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = projectName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (projectInfo.state.isNotEmpty()) {
                Row {
                    Text(
                        text = "Project status: ",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                    )
                    Text(
                        text = projectInfo.state,
                        style = MaterialTheme.typography.titleSmall,
                        color = when (projectInfo.state) {
                            "ACTIVE" -> Color(0xFF68D593)
                            "HIDDEN" -> Color(0xFFFF7272)
                            else -> Color(0xFFDE7411)
                        }
                    )
                }
            }
            if (projectInfo.description.isNotEmpty()) {
                Text(
                    text = "Description: " + projectInfo.description,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}