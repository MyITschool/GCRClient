package com.elseboot3909.gcrclient.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface ProgressBarInterface {

    var requests: Int
    var isRunning: MutableLiveData<Boolean>

    fun request() {
        requests++
        validate()
    }

    fun release() {
        requests--
        validate()
    }

    private fun validate() {
        if (requests == 0 && isRunning.value != false) {
            isRunning.postValue(false)
        } else if (requests != 0 && isRunning.value != true) {
            isRunning.postValue(true)
        }
    }

    fun getStatus(): LiveData<Boolean> {
        return isRunning
    }

}