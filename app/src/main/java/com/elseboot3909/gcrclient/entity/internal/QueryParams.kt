package com.elseboot3909.gcrclient.entity.internal

data class QueryParams(
    val q: String = "",
    val n: Int = 0,
    val S: Int = 0,
    val o: ArrayList<String> = ArrayList()
)