package com.elseboot3909.gcrclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LinesChangedCount(inserted: String, deleted: String) {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(20.dp)
            .background(
                shape = RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp),
                color = Color(0xFFA8D784)
            )
    ) {
        Text(
            text = inserted,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(20.dp)
            .background(
                shape = RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp),
                color = Color(0xFFEF7C77)
            )
    ) {
        Text(
            text = deleted,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

fun changedCountString(value: Int): String {
    return if (value > 999) "999+" else value.toString()
}