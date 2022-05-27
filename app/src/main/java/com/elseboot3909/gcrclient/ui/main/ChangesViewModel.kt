package com.elseboot3909.gcrclient.ui.main

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.gcrclient.api.ChangesAPI
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangesViewModel : ViewModel() {

    private val changesList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>()
    }

    fun getChangesList(params: ArrayList<String>, offset: Int): LiveData<ArrayList<ChangeInfo>> {
        loadChangesList(params, offset)
        return changesList
    }

    private fun loadChangesList(params: ArrayList<String>, offset: Int) {
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        val request = if (params.isNotEmpty()) {
            retrofit.create(ChangesAPI::class.java).queryChanges(q = TextUtils.join("+", params), n = 30, S = offset)
        } else {
            retrofit.create(ChangesAPI::class.java).queryChanges(n = 30, S = offset)
        }

        request.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body() != null) {
                        changesList.postValue(
                            Gson().fromJson(
                                JsonUtils.trimJson(response.body()),
                                object : TypeToken<ArrayList<ChangeInfo>>() {}.type
                            )
                        )
                    } else {
                        Log.e(
                            "${Constants.LOG_TAG} (${this.javaClass.name})",
                            "onResponse: Not successful ${response.raw()}"
                        )
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e(
                        "${Constants.LOG_TAG} (${this.javaClass.name})",
                        "onFailure: Not successful"
                    )
                }
            })
    }

}