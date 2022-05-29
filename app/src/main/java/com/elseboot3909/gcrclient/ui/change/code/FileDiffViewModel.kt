package com.elseboot3909.gcrclient.ui.change.code

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.gcrclient.api.ChangesAPI
import com.elseboot3909.gcrclient.entity.DiffInfo
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FileDiffViewModel : ViewModel() {

    private val diffInfo: MutableLiveData<DiffInfo> by lazy {
        MutableLiveData<DiffInfo>()
    }
    private val _file: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    fun getDiffInfo(id: String, revision: String, base: Int, file: String): LiveData<DiffInfo> {
        if (_file.value != file ) {
            _file.value = file
            loadDiffInfo(id, revision, base, file)
        }
        return diffInfo
    }

    private fun loadDiffInfo(id: String, revision: String, base: Int, file: String) {
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        val request = if (base == 0) {
            retrofit.create(ChangesAPI::class.java).getFileDiff(id = id, revision = revision, file = Uri.encode(file))
        } else {
            retrofit.create(ChangesAPI::class.java).getFileDiff(id = id, revision = revision, base = base, file = Uri.encode(file))
        }

        request.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.e(Constants.LOG_TAG, response.raw().toString())
                    if (response.isSuccessful && response.body() != null) {
                        diffInfo.postValue(
                            Gson().fromJson(
                                JsonUtils.trimJson(response.body()),
                                DiffInfo::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }
            })
    }

}