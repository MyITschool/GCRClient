package com.elseboot3909.gcrclient.entity

import java.io.Serializable

data class RevisionInfo(
    val _number: Int = 0,
    val commit: CommitInfo = CommitInfo(),
) : Serializable, Comparator<RevisionInfo> {
    override fun compare(o1: RevisionInfo?, o2: RevisionInfo?): Int {
        return when {
            o1 == null || o2 == null -> 0
            o1._number > o2._number -> 1
            o2._number > o1._number -> -1
            else -> 0
        }
    }
}