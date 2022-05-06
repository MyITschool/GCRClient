package com.elseboot3909.GCRClient.Entities

import java.io.Serializable
import kotlin.collections.ArrayList

data class LabelInfo(
        val all: ArrayList<ApprovalInfo> = ArrayList()
)  : Serializable
