package com.elseboot3909.GCRClient.Utils

import com.elseboot3909.GCRClient.Entities.ServerData
import okhttp3.*

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class NetManager {

    companion object {
        private fun getAuthenticatorClient(serverData: ServerData) : OkHttpClient{
            return OkHttpClient.Builder().authenticator { _, response ->
                response.request().newBuilder()
                        .header("Authorization", Credentials.basic(serverData.username, serverData.password))
                        .build()
            }.followRedirects(false)
                    .followSslRedirects(false)
                    .build()
        }


        fun getRetrofitConfiguration(serverData: ServerData? , auth: Boolean) : Retrofit {
            val sData = serverData ?: ServerDataManager.serverDataList[ServerDataManager.selectedPos]
            val retrofitBuilder = Retrofit.Builder()
                    .baseUrl(sData.serverURL + sData.prefixURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
            if (auth) {
                retrofitBuilder.client(getAuthenticatorClient(sData))
            }
            return retrofitBuilder.build()
        }
    }
}
