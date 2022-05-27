package com.elseboot3909.gcrclient.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.gcrclient.api.ChangesAPI
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StarredViewModel : ViewModel() {

    private val starredList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>().also {
            loadStarredChanges()
        }
    }

    fun getStarredList(): LiveData<ArrayList<ChangeInfo>> {
        return starredList
    }

    private fun loadStarredChanges() {
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        retrofit.create(ChangesAPI::class.java).queryChanges(q = "is:starred")
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body() != null) {
                        starredList.postValue(
                            Gson().fromJson(
                                JsonUtils.trimJson(response.body()),
                                object : TypeToken<ArrayList<ChangeInfo>>() {}.type
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }
            })
    }
}