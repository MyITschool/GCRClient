package com.elseboot3909.gcrclient.entity

data class DiffInfo(
    val meta_a: DiffFileMetaInfo = DiffFileMetaInfo(),
    val meta_b: DiffFileMetaInfo = DiffFileMetaInfo(),
    val change_type: String = "MODIFIED",
    val content: ArrayList<DiffContent> = ArrayList()
)