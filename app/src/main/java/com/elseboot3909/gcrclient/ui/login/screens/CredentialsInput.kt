@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.login.screens

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.credentials.PasswordCrypto
import com.elseboot3909.gcrclient.credentials.dataStore.CredentialsDataStore
import com.elseboot3909.gcrclient.entity.external.AccountInfo
import com.elseboot3909.gcrclient.remote.api.AccountAPI
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.MasterScreens
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

@Composable
internal fun CredentialsInput(
    encodedServerURL: String,
    isInit: Boolean,
    masterNavCtl: NavHostController
) {
    val serverURL = Uri.decode(encodedServerURL)
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as MasterActivity
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
        var username by remember { mutableStateOf("") }
        var isUsernameError by remember { mutableStateOf(false) }
        Column(modifier = Modifier.wrapContentHeight()) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it.trim()
                    isUsernameError = false
                },
                label = { Text(text = "Username") },
                isError = isUsernameError,
                singleLine = true,
                modifier = Modifier
                    .width(if (screenWidth * 0.7 > 256) 256.dp else (screenWidth * 0.7).dp)
            )
            if (isUsernameError) {
                Text(
                    text = "Failed to verify username",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 14.dp)
                )
            }
        }
        var password by remember { mutableStateOf("") }
        var isPasswordError by remember { mutableStateOf(false) }
        Column(modifier = Modifier.wrapContentHeight().padding(bottom = 16.dp)) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it.trim()
                    isPasswordError = false
                },
                label = { Text(text = "Password") },
                isError = isPasswordError,
                singleLine = true,
                modifier = Modifier
                    .width(if (screenWidth * 0.7 > 256) 256.dp else (screenWidth * 0.7).dp)
            )
            if (isPasswordError) {
                Text(
                    text = "Wrong password",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 14.dp)
                )
            }
        }
        Button(onClick = {
            scope.launch {
                showProgress = true
                var response = AccountAPI.getAccount(
                    ServerData.newBuilder().setServerURL(serverURL).build(), AccountInfo(username = username)
                )
                if (response.status.value in 200..299) {
                    val serverData = ServerData
                        .newBuilder()
                        .setServerURL(serverURL)
                        .setUsername(username)
                        .setPassword(PasswordCrypto.encryptString(password))
                        .build()
                    response = AccountAPI.getAccountDetails(serverData, AccountInfo(username = username))
                    if (response.status.value in 200..299) {
                        serverData?.let {
                            activity.get<CredentialsDataStore>().addServerData(it)
                        }
                        if(isInit) {
                            masterNavCtl.navigate(route = MasterScreens.HomeScreen.route)
                        } else {
                            activity.restartRequest()
                        }
                    } else {
                        isPasswordError = true
                    }
                } else {
                    isUsernameError = true
                }
                showProgress = false
            }
        }) {
            Text(text = "Let's go")
        }
    }
}