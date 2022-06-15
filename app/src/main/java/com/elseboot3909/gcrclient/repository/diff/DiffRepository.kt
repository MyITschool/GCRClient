package com.elseboot3909.gcrclient.repository.diff

import kotlinx.coroutines.flow.MutableStateFlow

class DiffRepository {
    val base = MutableStateFlow(0)
    val revision = MutableStateFlow("")
    val file = MutableStateFlow("")
}