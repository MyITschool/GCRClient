package com.elseboot3909.gcrclient.ui.main.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.utils.Constants

@ExperimentalMaterial3Api
@Composable
fun MainSearch(
    navController: NavHostController,
    searchParams: SearchParams,
    searchProjects: ArrayList<String>
) {
    var searchStr by rememberSaveable { mutableStateOf(searchParams.strSearch) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val backgroundColor =
        if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = backgroundColor) {
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
            ExtendedFloatingActionButton(
                text = { Text(text = "Search") },
                icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                onClick = {
                    (context as SearchActivity).let {
                        if (searchParams.strSearch != searchStr) {
                            it.intent.putExtra(Constants.SEARCH_STRING_KEY, searchStr)
                            it.setResult(Constants.SEARCH_ACQUIRED, it.intent)
                        }
                        it.finish()
                    }
                })
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 42.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Project", style = MaterialTheme.typography.titleMedium)
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
                            text = if (searchProjects.size == 0) "Select project" else searchProjects[0],
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
    BackHandler(true) {
        (context as SearchActivity).let {
            it.setResult(Constants.EMPTY_COMMAND)
            it.finish()
        }
    }
}