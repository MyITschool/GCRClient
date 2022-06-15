@file:OptIn(ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.search.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.external.AccountInfo
import com.elseboot3909.gcrclient.entity.internal.getAvatar
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.AccountUtils.Companion.getShowedName
import com.elseboot3909.gcrclient.repository.search.SearchParamsRepository
import com.elseboot3909.gcrclient.viewmodel.search.UsersListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun UsersListContent(navController: NavHostController) {
    UsersListTopAppBar(navController)
}

@Composable
private fun UsersListTopAppBar(
    navController: NavHostController,
    usersModel: UsersListViewModel = getViewModel(),
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
            val selectedCount = searchParamsRepo.selectedUsersCounter.collectAsState()
            ExtendedFloatingActionButton(
                text = { Text(text = "Select ${selectedCount.value} " + if (selectedCount.value == 1) "user" else "users") },
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
            UsersList(usersModel, searchStr)
        }
    }
}


@Composable
private fun UsersList(
    usersModel: UsersListViewModel,
    searchStr: String
) {
    val users: ArrayList<AccountInfo> by usersModel.users.observeAsState(ArrayList())
    val usersList = users.map { n -> n }.filter { n -> n.toString().contains(searchStr) || searchStr.isEmpty() }
    SwipeRefresh(
        state = rememberSwipeRefreshState(false),
        onRefresh = { usersModel.refreshUsers() }) {
        LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
            items(
                count = usersList.size,
                itemContent = {
                    UserListItem(usersList[it])
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
private fun UserListItem(
    accountInfo: AccountInfo,
    searchParamsRepo: SearchParamsRepository = get()
) {
    var isSelected by remember { mutableStateOf(searchParamsRepo.selectedUsers.contains(accountInfo._account_id)) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                isSelected = !isSelected
                if (isSelected) {
                    searchParamsRepo.appendSelectedUser(accountInfo._account_id)
                } else {
                    searchParamsRepo.removeSelectedUser(accountInfo._account_id)
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F7EA) else colorResource(R.color.neutral_1_100)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .defaultMinSize(minHeight = 32.dp)
                .padding(top = 4.dp, bottom = 4.dp),
        ) {
            AsyncImage(
                model = getAvatar(accountInfo.avatars),
                contentDescription = null,
                error = painterResource(id = AccountUtils.getAvatarById(accountInfo._account_id)),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 6.dp)) {
                Text(
                    text = getShowedName(accountInfo),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                )
                Text(
                    text =  "Email: ${accountInfo.email.ifEmpty { "Not set" }}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                )
            }
        }
    }
}