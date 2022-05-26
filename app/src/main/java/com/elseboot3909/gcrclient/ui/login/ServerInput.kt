package com.elseboot3909.gcrclient.ui.login

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.api.ConfigAPI
import com.elseboot3909.gcrclient.entity.ServerData
import com.elseboot3909.gcrclient.utils.NetManager
import com.elseboot3909.gcrclient.utils.ServerDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL


@Composable
fun ServerInput(navController: NavController) {
    var showProgress by remember { mutableStateOf(false) }
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp
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
            for (serverData in ServerDataManager.serverDataList) {
                if (serverData.serverURL == serverURL) {
                    showProgress = false
                    msgError = "Already logged in"
                    isError = true
                    break
                }
            }
            if (showProgress) {
                try {
                    URL(serverURL).toURI()
                    val retrofit =
                        NetManager.getRetrofitConfiguration(ServerData("", "", serverURL), false)
                    val config = retrofit.create(ConfigAPI::class.java)

                    config.getVersion().enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {
                                navController.navigate(route = "${Screens.Login.route}/${Uri.encode(serverURL)}")
                            } else {
                                msgError = "Unknown error, try again"
                                isError = true
                            }
                            showProgress = false
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            msgError = "Failed to get response from server"
                            isError = true
                            showProgress = false
                        }

                    })
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
