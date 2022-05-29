package com.elseboot3909.gcrclient.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.gcrclient.api.ChangesAPI
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.ui.common.ProgressBarInterface
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangesViewModel : ViewModel(), ProgressBarInterface {

    private val changesList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>()
    }

    override var requests = 0
    override var isRunning: MutableLiveData<Boolean> = MutableLiveData(false)

    private var _params = ""
    private var _offset = -1

    fun getChangesList(params: String, offset: Int): LiveData<ArrayList<ChangeInfo>> {
        if (_offset != offset || _params != params) {
            _params = params
            _offset = offset
            loadChangesList(params, offset)
        }
        return changesList
    }

    private fun loadChangesList(params: String, offset: Int) {
        request()
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        val request = if (params.isNotEmpty()) {
            retrofit.create(ChangesAPI::class.java).queryChanges(
                q = params,
                n = Constants.MAX_FETCHED_CHANGES,
                S = offset
            )
        } else {
            retrofit.create(ChangesAPI::class.java).queryChanges(n = Constants.MAX_FETCHED_CHANGES, S = offset)
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
                }
                release()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                release()
            }
        })
    }

}