package com.elseboot3909.gcrclient.ui.account

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*

class FavIconViewModel : ViewModel() {

    private var favIconMap: HashMap<String, MutableLiveData<String>> = HashMap()

    fun getFavIcon(url: String): MutableLiveData<String>? {
        if (!favIconMap.containsKey(url)) {
            favIconMap[url] = MutableLiveData(if (url.endsWith("/")) "${url}favicon.ico" else "${url}/favicon.ico")
            loadFavIcon(url)
        }
        return favIconMap[url]
    }

    private fun loadFavIcon(url: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sc = Scanner(URL(url).openStream())
                var lineFound = false
                while(sc.hasNext()) {
                    val next = sc.next()
                    if (lineFound && next.contains("href=\"")) {
                        next.substring(next.indexOf("href=\"") + 7, next.indexOf("\">")).let {
                            Log.e(Constants.LOG_TAG, if (url.endsWith("/")) "${url}$it" else "${url}/$it")
                            favIconMap[url]?.postValue(if (url.endsWith("/")) "${url}$it" else "${url}/$it")
                        }
                        break
                    }
                    if (!next.contains("rel=\"icon\"")) {
                        continue
                    } else {
                        lineFound = true
                    }
                }
            }
        }

    }

}