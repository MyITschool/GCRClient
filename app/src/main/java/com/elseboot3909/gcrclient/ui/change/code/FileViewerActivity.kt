package com.elseboot3909.gcrclient.ui.change.code

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.DiffInfo
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.utils.Constants
import java.lang.Integer.max

class FileViewerActivity : AppCompatActivity() {

    private val patchsetA: Int by lazy {
        intent.getIntExtra(Constants.FILE_PATCHSET_A_KEY, 0)
    }

    private val patchsetB: String by lazy {
        intent.getStringExtra(Constants.FILE_PATCHSET_B_KEY) ?: ""
    }

    private val fileName: String by lazy {
        intent.getStringExtra(Constants.FILE_NAME_KEY) ?: ""
    }

    private val revisionId: String by lazy {
        intent.getStringExtra(Constants.FILE_CHANGE_ID_KEY) ?: ""
    }

    private var maxSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)

        setContent {
            MainTheme {
                FileViewer()
            }
        }
    }

    @Composable
    private fun FileViewer() {
        val model: FileDiffViewModel by viewModels()
        val diffInfo: DiffInfo by model.getDiffInfo(
            id = revisionId,
            base = patchsetA,
            file = fileName,
            revision = patchsetB
        ).observeAsState(DiffInfo())
        val lineOffset = ArrayList<LineOffset>()
        val ifWrapped = ArrayList<MutableState<Boolean>>()
        lineOffset.add(LineOffset())
        diffInfo.content.forEachIndexed { part, content ->
            content.ab.forEachIndexed { i, line ->
                content.ab[i] = line.replace(' ', '\u00A0')
            }
            content.a.forEachIndexed { i, line ->
                content.a[i] = line.replace(' ', '\u00A0')
            }
            content.b.forEachIndexed { i, line ->
                content.b[i] = line.replace(' ', '\u00A0')
            }
            lineOffset.add(
                LineOffset(
                    content.ab.size + content.a.size + lineOffset[part].offsetA,
                    content.ab.size + content.b.size + lineOffset[part].offsetB
                )
            )
            ifWrapped.add(remember { mutableStateOf(content.ab.size > 25) })
        }
        maxSize = max(diffInfo.meta_a.lines, diffInfo.meta_b.lines).toString().length
        Column {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                diffInfo.content.forEachIndexed { part, content ->
                    if (ifWrapped[part].value) {
                        if (part != 0) {
                            for (i in 0..10) {
                                item {
                                    LineText(
                                        countA = "${i + 1 + lineOffset[part].offsetA}",
                                        countB = "${i + 1 + lineOffset[part].offsetB}",
                                        line = content.ab[i]
                                    )
                                }
                            }
                        }
                        item {
                            Button(onClick = { ifWrapped[part].value = false }, modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp), shape = RoundedCornerShape(4.dp)) {
                                Text(text = "Expand ${content.ab.size - 20} lines")
                            }
                        }
                        if (part + 1 != diffInfo.content.size) {
                            for (i in content.ab.size - 10 until content.ab.size) {
                                item {
                                    LineText(
                                        countA = "${i + 1 + lineOffset[part].offsetA}",
                                        countB = "${i + 1 + lineOffset[part].offsetB}",
                                        line = content.ab[i]
                                    )
                                }
                            }
                        }
                    } else {
                        content.ab.forEachIndexed { i, line ->
                            item {
                                LineText(
                                    countA = "${i + 1 + lineOffset[part].offsetA}",
                                    countB = "${i + 1 + lineOffset[part].offsetB}",
                                    line = line
                                )
                            }
                        }
                    }
                    content.a.forEachIndexed { i, line ->
                        item {
                            LineText(
                                countA = "${i + 1 + content.ab.size + lineOffset[part].offsetA}",
                                countB = "",
                                line = line,
                                background = Color(0xFFFFB0B0)
                            )
                        }
                    }
                    content.b.forEachIndexed { i, line ->
                        item {
                            LineText(
                                countA = "",
                                countB = "${i + 1 + content.ab.size + lineOffset[part].offsetB}",
                                line = line,
                                background = Color(0xFFE3F7EA)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LineText(
        countA: String = "",
        countB: String = "",
        line: String = "",
        background: Color = Color.Transparent
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .background(color = background)
        ) {
            Row(modifier = Modifier.background(color = colorResource(R.color.neutral_1_50))) {
                listOf(countA, countB).forEach {
                    Divider(
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF21005D)
                                )
                            ) {
                                append(it)
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.0f
                                    )
                                )
                            ) {
                                append("0".repeat(maxSize - it.length))
                            }
                        }
                    )
                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                }
            }
            Row(modifier = Modifier.padding(start = 3.dp)) {
                Text(text = line, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }
}

data class LineOffset(val offsetA: Int = 0, val offsetB: Int = 0)
