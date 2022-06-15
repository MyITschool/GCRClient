@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.switcher

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.credentials.dataStore.SelectedDataStore
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.common.SetBackground
import com.elseboot3909.gcrclient.viewmodel.credentials.CredentialsViewModel
import com.elseboot3909.gcrclient.viewmodel.home.ChangesViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun SwitcherScreenContent(navController: NavHostController) {
    SetBackground()
    ServerList(navController)
}

@Composable
private fun ServerList(
    navController: NavHostController,
    credentialsViewModel: CredentialsViewModel = getViewModel(),
    changesViewModel: ChangesViewModel = getViewModel(),
    selectedDataStore: SelectedDataStore = get()
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val serversList = credentialsViewModel.serversList.observeAsState()
    val activity = LocalContext.current as MasterActivity
    val finalServersList: List<ServerData> = serversList.value?.serverDataList?.mapNotNull { it } ?: ArrayList()
    Column(
        Modifier
            .fillMaxHeight()
            .padding(top = (screenHeight * 0.45).dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = "Select server",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 28.dp, end = 28.dp, bottom = 48.dp, top = 72.dp)
        ) {
            itemsIndexed(items = finalServersList, itemContent = { index, item ->
                Row {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            selectedDataStore.updateSelected(index)
                            activity.restartRequest()
                        },
                        modifier = Modifier.defaultMinSize(minHeight = 42.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
                                .defaultMinSize(minHeight = 42.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.serverURL,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            })
        }
    }
}