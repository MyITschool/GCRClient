package com.elseboot3909.gcrclient.ui.common.progress

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.viewmodel.progress.ProgressBarViewModel
import org.koin.androidx.compose.get

@Composable
fun ProgressBar(
    overlaps: Boolean = true,
    progressBarViewModel: ProgressBarViewModel = get()
) {
    if (!overlaps) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ProgressBarContent(progressBarViewModel)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            ProgressBarContent(progressBarViewModel)
        }
    }
}

@Composable
private fun ProgressBarContent(progressBarViewModel: ProgressBarViewModel) {
    val visible = progressBarViewModel.isVisible.observeAsState(false)
    Log.e("(GCR)ProgressBarContent()", visible.value.toString())
    if (visible.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}