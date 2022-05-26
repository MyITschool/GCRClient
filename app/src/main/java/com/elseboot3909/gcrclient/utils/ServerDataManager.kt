package com.elseboot3909.gcrclient.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.elseboot3909.gcrclient.entity.ServerData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.security.GeneralSecurityException

class ServerDataManager {

    companion object {

        lateinit var serverDataList: ArrayList<ServerData>
        var selectedPos = 0

        private val gson = Gson()

        private fun getSharedPreferences(context: Context, name: String): SharedPreferences? {
            var sharedPreferences: SharedPreferences? = null
            try {
                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                sharedPreferences = EncryptedSharedPreferences.create(
                    name,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
            }

            return sharedPreferences
        }

        @SuppressLint("ApplySharedPref")
        fun writeServerDataList(context: Context) {
            val jsonArray = gson.toJsonTree(
                serverDataList,
                object : TypeToken<MutableList<ServerData>>() {}.type
            )
            getSharedPreferences(context, Constants.SERVERS_DATA)?.edit()
                ?.putString(Constants.SERVERS_DATA, jsonArray.toString())?.commit()
        }

        fun loadServerDataList(context: Context) {
            val mValue = Constants.SERVERS_DATA
            if (getSharedPreferences(context, mValue) != null && getSharedPreferences(
                    context,
                    mValue
                )?.getString(mValue, "")?.isNotEmpty() == true
            ) {
                try {
                    serverDataList = gson.fromJson(
                        getSharedPreferences(context, mValue)?.getString(mValue, ""),
                        object : TypeToken<MutableList<ServerData>>() {}.type
                    )
                } catch (ignored: JsonSyntaxException) {
                    serverDataList.clear()
                    serverDataList.add(
                        gson.fromJson(
                            getSharedPreferences(
                                context,
                                mValue
                            )?.getString(mValue, ""), ServerData::class.java
                        )
                    )
                }
            } else {
                serverDataList = ArrayList()
            }
        }

        @SuppressLint("ApplySharedPref")
        fun writeNewPosition(context: Context, pos: Int) {
            if (pos >= 0 && serverDataList.size > pos) {
                selectedPos = pos
                getSharedPreferences(context, Constants.SELECTED_SERVER)?.edit()
                    ?.putInt(Constants.SELECTED_SERVER, pos)?.commit()
            } else {
                Log.e(
                    "${Constants.LOG_TAG} (${this::class.java.name})",
                    "Failed to writeNewPosition(), pos=$pos , selectedPos=$selectedPos , size=$serverDataList.size"
                )
            }
        }

        fun loadSavedPosition(context: Context) {
            selectedPos = getSharedPreferences(
                context,
                Constants.SELECTED_SERVER
            )?.getInt(Constants.SELECTED_SERVER, -1) ?: -1
        }

    }
}
