package com.elseboot3909.gcrclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.ui.theme.deletionsColor
import com.elseboot3909.gcrclient.ui.theme.insertionsColor

@Composable
fun LinesChangedCount(inserted: String, deleted: String) {
        Text(
            text = inserted,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(32.dp)
                .height(20.dp)
                .background(
                    shape = RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp),
                    color = insertionsColor
                )
        )
        Text(
            text = deleted,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(32.dp)
                .height(20.dp)
                .background(
                    shape = RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp),
                    color = deletionsColor
                )
        )
}

fun changedCountString(value: Int): String {
    return if (value > 999) "999+" else value.toString()
}