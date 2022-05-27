package com.elseboot3909.gcrclient.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.elseboot3909.gcrclient.R

@Composable
fun LinesChangedCount(inserted: Int, deleted: Int) {
    Box {
        Image(
            painter = painterResource(id = R.drawable.ic_insertions_background),
            contentDescription = null
        )
        Text(
            text = changedCountString(inserted),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    Box {
        Image(
            painter = painterResource(id = R.drawable.ic_deletions_background),
            contentDescription = null
        )
        Text(
            text = changedCountString(deleted),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun changedCountString(value: Int): String {
    return if (value > 999) "999+" else value.toString()
}