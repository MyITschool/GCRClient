package com.elseboot3909.gcrclient.ui.login

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
import com.elseboot3909.gcrclient.api.AccountAPI
import com.elseboot3909.gcrclient.entity.AccountInfo
import com.elseboot3909.gcrclient.entity.ServerData
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.elseboot3909.gcrclient.utils.ServerDataManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalAnimationApi
@Composable
fun Login(encodedServerURL: String) {
    val serverURL = Uri.decode(encodedServerURL)
    val context = LocalContext.current
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
        Column {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it.trim()
                    isUsernameError = false
                },
                label = { Text(text = "Username") },
                isError = isUsernameError,
                singleLine = true,
                modifier = Modifier.padding(bottom = 16.dp).width(if (screenWidth * 0.7 > 256) 256.dp else (screenWidth * 0.7).dp)
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
        Column {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it.trim()
                    isPasswordError = false
                },
                label = { Text(text = "Password") },
                isError = isPasswordError,
                singleLine = true,
                modifier = Modifier.padding(bottom = 48.dp).width(if (screenWidth * 0.7 > 256) 256.dp else (screenWidth * 0.7).dp)
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
            showProgress = true
            var retrofit =
                NetManager.getRetrofitConfiguration(ServerData("", "", serverURL), false)

            retrofit.create(AccountAPI::class.java)
                .getAccountInfo(username)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful && response.body()
                                ?.contains("not found") != true
                        ) {
                            username = Gson().fromJson(
                                JsonUtils.trimJson(response.body()),
                                AccountInfo::class.java
                            ).username

                            retrofit = NetManager.getRetrofitConfiguration(
                                ServerData(
                                    username,
                                    password,
                                    serverURL
                                ), true
                            )

                            retrofit.create(AccountAPI::class.java).getSelfAccountDetails()
                                .enqueue(object : Callback<String> {
                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        if (response.isSuccessful && response.body()
                                                ?.contains("Unauthorized") != true
                                        ) {
                                            val accountInfo = Gson().fromJson(
                                                JsonUtils.trimJson(response.body()),
                                                AccountInfo::class.java
                                            )
                                            ServerDataManager.serverDataList.add(
                                                ServerData(
                                                    accountInfo.username,
                                                    password,
                                                    serverURL
                                                )
                                            )
                                            context.let {
                                                ServerDataManager.writeServerDataList(it)
                                                ServerDataManager.writeNewPosition(
                                                    it,
                                                    ServerDataManager.serverDataList.size - 1
                                                )
                                            }
                                            (context as LoginActivity).let {
                                                it.setResult(Constants.ACCOUNT_SWITCHED)
                                                it.finish()
                                            }
                                        } else {
                                            isPasswordError = true
                                        }
                                        showProgress = false
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        isPasswordError = false
                                        showProgress = false
                                    }

                                })
                        } else {
                            isUsernameError = true
                            showProgress = false
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        isUsernameError = true
                        showProgress = false
                    }

                })

        }) {
            Text(text = "Let's go")
        }
    }
}