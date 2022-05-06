package com.elseboot3909.GCRClient.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.GCRClient.API.AccountAPI
import com.elseboot3909.GCRClient.Entities.AccountInfo
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.JsonUtils
import com.elseboot3909.GCRClient.Utils.NetManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AccountViewModel : ViewModel() {

    private var accountInfoMap: HashMap<String, MutableLiveData<AccountInfo>> = HashMap<String, MutableLiveData<AccountInfo>>()

    fun getAccountInfo(id: String) : MutableLiveData<AccountInfo>? {
        if (!accountInfoMap.containsKey(id)) {
            accountInfoMap[id] = MutableLiveData<AccountInfo>()
            loadProfileInfo(id)
        }
        return accountInfoMap[id]
    }

    private fun loadProfileInfo(id: String) {
            val retrofit = NetManager.getRetrofitConfiguration(null, true)

            val accountAPI = retrofit.create(AccountAPI::class.java)
            val retrofitRequest = accountAPI.getAccountInfo(id)

            retrofitRequest.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body() != null) {
                        accountInfoMap[id]?.postValue(Gson().fromJson(JsonUtils.trimJson(response.body()), AccountInfo::class.java))
                    } else {
                        Log.e(Constants.LOG_TAG, "onResponse: Not successful")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e(Constants.LOG_TAG, "onFailure: Not successful")
                }
            })
    }

}
