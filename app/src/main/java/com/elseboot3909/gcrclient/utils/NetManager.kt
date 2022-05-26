package com.elseboot3909.gcrclient.utils

import com.elseboot3909.gcrclient.entity.ServerData
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class NetManager {

    companion object {
        private fun getAuthenticatorClient(serverData: ServerData): OkHttpClient {
            return OkHttpClient.Builder().authenticator { _, response ->
                response.request.newBuilder()
                    .header(
                        "Authorization",
                        Credentials.basic(serverData.username, serverData.password)
                    )
                    .build()
            }.followRedirects(false)
                .followSslRedirects(false)
                .build()
        }


        fun getRetrofitConfiguration(serverData: ServerData?, auth: Boolean): Retrofit {
            val sData =
                serverData ?: ServerDataManager.serverDataList[ServerDataManager.selectedPos]
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(sData.serverURL)
                .addConverterFactory(ScalarsConverterFactory.create())
            if (auth) {
                retrofitBuilder.client(getAuthenticatorClient(sData))
            }
            return retrofitBuilder.build()
        }
    }
}
