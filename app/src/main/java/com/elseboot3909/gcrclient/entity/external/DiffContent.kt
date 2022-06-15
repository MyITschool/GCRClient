package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class DiffContent(
    var a: ArrayList<String> = ArrayList(),
    var b: ArrayList<String> = ArrayList(),
    var ab: ArrayList<String> = ArrayList()
)