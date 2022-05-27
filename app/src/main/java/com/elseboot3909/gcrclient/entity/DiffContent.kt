package com.elseboot3909.gcrclient.entity

data class DiffContent(
    var a: ArrayList<String> = ArrayList(),
    var b: ArrayList<String> = ArrayList(),
    var ab: ArrayList<String> = ArrayList()
)