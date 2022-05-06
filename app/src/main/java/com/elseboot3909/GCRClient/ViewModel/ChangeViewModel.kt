package com.elseboot3909.GCRClient.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.GCRClient.API.ChangesAPI
import com.elseboot3909.GCRClient.Entities.ChangeInfo
import com.elseboot3909.GCRClient.Entities.CommitInfo
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.JsonUtils
import com.elseboot3909.GCRClient.Utils.NetManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeViewModel : ViewModel() {

    private var id = ""

    private val changeInfo: MutableLiveData<ChangeInfo> by lazy {
        MutableLiveData<ChangeInfo>().also {
            loadChangeInfo()
        }
    }

    private val commitInfo: MutableLiveData<CommitInfo> by lazy {
        MutableLiveData<CommitInfo>().also {
            loadCommitInfo()
        }
    }

    private val gson = Gson()

    fun getChangeInfo(id: String) : LiveData<ChangeInfo> {
        if (id != this.id) {
            this.id = id
            loadChangeInfo()
        }
        return changeInfo
    }

    fun getCommitInfo(id: String) : LiveData<CommitInfo> {
        if (id != this.id) {
            this.id = id
            loadCommitInfo()
        }
        return commitInfo
    }

    private fun loadChangeInfo() {
            val retrofit = NetManager.getRetrofitConfiguration(null, true)

            val changesAPI = retrofit.create(ChangesAPI::class.java)
            changesAPI.getChangeDetails(id).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body() != null) {
                        changeInfo.postValue(gson.fromJson(JsonUtils.trimJson(response.body()), ChangeInfo::class.java))
                    } else {
                        Log.e(Constants.LOG_TAG, "onResponse: Not successful")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e(Constants.LOG_TAG, "onFailure: Not successful")
                }
            })
    }


    private fun loadCommitInfo() {
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        val changesAPI = retrofit.create(ChangesAPI::class.java)
        changesAPI.getCommitInfo(id, "current").enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null) {
                    commitInfo.postValue(gson.fromJson(JsonUtils.trimJson(response.body()), CommitInfo::class.java))
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
