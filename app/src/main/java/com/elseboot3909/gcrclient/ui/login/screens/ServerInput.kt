@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.login.screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.remote.api.ConfigAPI
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.login.Screens
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.CredentialsViewModel
import io.ktor.client.call.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import java.net.URL

@Composable
internal fun ServerInput(
    navController: NavController,
    credentialsViewModel: CredentialsViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    val scope = rememberCoroutineScope()
    val serversList = credentialsViewModel.serversList.observeAsState()
    val finalServersList: List<ServerData> = serversList.value?.serverDataList?.mapNotNull { it } ?: ArrayList()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var showProgress by remember { mutableStateOf(false) }
    if (showProgress) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    Column(
        Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .padding(start = 32.dp, top = (screenHeight * 0.45).dp)
    ) {
        Text(
            "Login",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var serverURL by rememberSaveable { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var msgError by remember { mutableStateOf("") }
        Column(modifier = Modifier.padding(bottom = 48.dp)) {
            OutlinedTextField(
                value = serverURL,
                onValueChange = {
                    serverURL = it.trim()
                    isError = false
                    msgError = "Incorrect server address"
                },
                label = { Text(text = "Server") },
                singleLine = true,
                isError = isError,
                modifier = Modifier.width(if (screenWidth * 0.7 > 256) 256.dp else (screenWidth * 0.7).dp)
            )
            if (isError) {
                Text(
                    text = msgError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 14.dp)
                )
            }
        }
        Button(onClick = {
            showProgress = true
            for (serverData in finalServersList) {
                if (serverData.serverURL.contains(serverURL) || serverURL.contains(serverData.serverURL)) {
                    showProgress = false
                    msgError = "Already logged in"
                    isError = true
                    break
                }
            }
            if (showProgress) {
                try {
                    URL(serverURL).toURI()
                    scope.launch {
                        val response = ConfigAPI.getVersion(ServerData.newBuilder().setServerURL(serverURL).build())
                        if (response.status.value in 200..299) {
                            navController.navigate(route = "${Screens.Login.route}/${Uri.encode(serverURL)}")
                        } else {
                            Log.e(Constants.LOG_TAG, response.body())
                            msgError = "Failed to get response from server"
                            isError = true
                        }
                        showProgress = false
                    }
                } catch (e: Exception) {
                    showProgress = false
                    msgError = "Incorrect server address"
                    isError = true
                }
            }
        }) {
            Text(text = "Check connection")
        }
    }

}
