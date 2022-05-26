package com.elseboot3909.gcrclient.ui.change

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.gcrclient.api.ChangesAPI
import com.elseboot3909.gcrclient.entity.FileInfo
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilesViewModel : ViewModel() {

    private var _id: MutableLiveData<String> = MutableLiveData("")
    private var _revision: MutableLiveData<String> = MutableLiveData("")
    private var _base: MutableLiveData<Int> = MutableLiveData(0)

    private val filesList: MutableLiveData<HashMap<String, FileInfo>> by lazy {
        MutableLiveData<HashMap<String, FileInfo>>()
    }

    fun getFilesList(id: String, revision: String, base: Int): LiveData<HashMap<String, FileInfo>> {
        if (id != _id.value || revision != _revision.value || base != _base.value) {
            _base.value = base
            _revision.value = revision
            _id.value = id
            loadFilesList(id, revision, base)
        }
        return filesList
    }

    private fun loadFilesList(id: String, revision: String, base: Int) {
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        val request = if (base == 0) {
            retrofit.create(ChangesAPI::class.java).getFilesList(id, revision)
        } else {
            retrofit.create(ChangesAPI::class.java).getFilesList(id, revision, base)
        }
        request.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null) {
                    filesList.postValue(
                        Gson().fromJson(
                            JsonUtils.trimJson(response.body()),
                            object : TypeToken<HashMap<String, FileInfo>>() {}.type
                        )
                    )
                    android.util.Log.e(Constants.LOG_TAG, "onResponse: Good" + response.body())
                } else {
                    android.util.Log.e(Constants.LOG_TAG, "onResponse: Bad" + response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                android.util.Log.e(Constants.LOG_TAG, "onFailure: Bad")
            }
        })
    }

}