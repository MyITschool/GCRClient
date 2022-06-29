@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.common.progress

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.viewmodel.ProgressBarViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ProgressBar(
    progressBarViewModel: ProgressBarViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        ProgressBarContent(progressBarViewModel)
    }
}

@Composable
private fun ProgressBarContent(progressBarViewModel: ProgressBarViewModel) {
    val visible = progressBarViewModel.isVisible.observeAsState(false)
    if (visible.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}