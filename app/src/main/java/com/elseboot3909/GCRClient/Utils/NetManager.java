package com.elseboot3909.GCRClient.Utils;

import com.elseboot3909.GCRClient.Entities.ServerData;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetManager {

    public static OkHttpClient getAuthenticatorClient(ServerData serverData) {
        return new OkHttpClient.Builder()
                .authenticator((route, resp) -> resp.request().newBuilder()
                        .header("Authorization", Credentials.basic(serverData.getUsername(), serverData.getPassword()))
                        .build())
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
    }


    public static Retrofit getRetrofitConfiguration(ServerData serverData, boolean auth) {
        if (serverData == null) {
            serverData = ServerDataManager.serverDataList.get(ServerDataManager.selectedPos);
        }
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(serverData.getURL())
                .addConverterFactory(ScalarsConverterFactory.create());
        if (auth) {
            retrofitBuilder.client(getAuthenticatorClient(serverData));
        }
        return retrofitBuilder.build();
    }

}
