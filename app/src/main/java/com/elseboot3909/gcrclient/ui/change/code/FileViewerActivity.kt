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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.DiffInfo
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.utils.Constants
import com.google.gson.Gson
import kotlin.math.max

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)

        setContent {
            MainTheme {
                Box(
                    modifier = Modifier
                        .background(color = Color(0xFF303030))
                        .fillMaxSize()
                ) {
                    FileViewer()
                }
            }
        }
    }

    @Composable
    private fun FileViewer() {
        val model: FileDiffViewModel by viewModels()
        val oldDiffInfo: DiffInfo by model.getDiffInfo(
            id = revisionId,
            base = patchsetA,
            file = fileName,
            revision = patchsetB
        ).observeAsState(DiffInfo())
        val diffInfo = Gson().fromJson(Gson().toJson(oldDiffInfo), DiffInfo::class.java)
        val maxSize = max(diffInfo.meta_a.lines, diffInfo.meta_b.lines).toString().length
        val parsedContent = ArrayList<ParsedPart>()
        var offsetA = 0
        var offsetB = 0
        diffInfo.content.forEachIndexed { part, content ->
            val parsedLinesBefore = ArrayList<ParsedLine>()
            val parsedLinesWrapped = ArrayList<ParsedLine>()
            val parsedLinesAfter = ArrayList<ParsedLine>()
            content.ab.forEachIndexed { i, line ->
                offsetA++; offsetB++
                val currentLine = ParsedLine(
                    countA = offsetA.toString(),
                    offsetA = "0".repeat(maxSize - offsetA.toString().length),
                    countB = offsetB.toString(),
                    offsetB = "0".repeat(maxSize - offsetB.toString().length),
                    text = line.replace(' ', '\u00A0')
                )
                if (content.ab.size > 25) {
                    if (i <= 10 && part != 0) {
                        parsedLinesBefore.add(currentLine)
                    } else if (i >= content.ab.size - 10 && diffInfo.content.size - 1 != part) {
                        parsedLinesAfter.add(currentLine)
                    } else {
                        parsedLinesWrapped.add(currentLine)
                    }
                } else {
                    parsedLinesAfter.add(currentLine)
                }
            }
            val parsedLinesA = ArrayList<ParsedLine>()
            content.a.forEach {
                offsetA++
                parsedLinesA.add(
                    ParsedLine(
                        countA = offsetA.toString(),
                        offsetA = "0".repeat(maxSize - offsetA.toString().length),
                        offsetB = "0".repeat(maxSize),
                        text = it.replace(' ', '\u00A0')
                    )
                )
            }
            val parsedLinesB = ArrayList<ParsedLine>()
            content.b.forEach {
                offsetB++
                parsedLinesB.add(
                    ParsedLine(
                        offsetA = "0".repeat(maxSize),
                        countB = offsetB.toString(),
                        offsetB = "0".repeat(maxSize - offsetB.toString().length),
                        text = it.replace(' ', '\u00A0')
                    )
                )
            }
            if (parsedLinesBefore.isNotEmpty()) {
                parsedContent.add(
                    ParsedPart(
                        backgroundColor = Color(0xFF303030),
                        content = parsedLinesBefore,
                        isWrapped = remember { mutableStateOf(false) })
                )
            }
            if (parsedLinesWrapped.isNotEmpty()) {
                parsedContent.add(
                    ParsedPart(
                        backgroundColor = Color(0xFF303030),
                        content = parsedLinesWrapped,
                        isWrapped = remember { mutableStateOf(true) },
                        wrappedText = "Expand ${parsedLinesWrapped.size} lines"
                    )
                )
            }
            if (parsedLinesAfter.isNotEmpty()) {
                parsedContent.add(
                    ParsedPart(
                        backgroundColor = Color(0xFF303030),
                        content = parsedLinesAfter,
                        isWrapped = remember { mutableStateOf(false) })
                )
            }
            if (parsedLinesA.isNotEmpty()) {
                parsedContent.add(
                    ParsedPart(
                        backgroundColor = Color(0xFFFFB0B0),
                        textColor = Color(0xFF414141),
                        content = parsedLinesA,
                        isWrapped = remember { mutableStateOf(false) })
                )
            }
            if (parsedLinesB.isNotEmpty()) {
                parsedContent.add(
                    ParsedPart(
                        backgroundColor = Color(0xFFE3F7EA),
                        textColor = Color(0xFF414141),
                        content = parsedLinesB,
                        isWrapped = remember { mutableStateOf(false) })
                )
            }
        }

        Column {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                parsedContent.forEach {
                    if (it.isWrapped.value) {
                        item {
                            Button(
                                onClick = { it.isWrapped.value = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 2.dp, end = 2.dp),
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                Text(text = it.wrappedText)
                            }
                        }
                    } else {
                        it.content.forEach { line ->
                            item {
                                LineText(line, it.backgroundColor, it.textColor)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LineText(parsedLine: ParsedLine, backgroundColor: Color, textColor: Color) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .background(color = backgroundColor)
        ) {
            Row(modifier = Modifier.background(color = Color(0xFF545454))) {
                LineCounter(parsedLine.countA, parsedLine.offsetA)
                LineCounter(parsedLine.countB, parsedLine.offsetB)
            }
            Row(modifier = Modifier.padding(start = 3.dp)) {
                Text(
                    text = parsedLine.text,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                )
            }
        }
    }

    @Composable
    private fun LineCounter(count: String, offset: String) {
        Divider(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.bodyMedium.toSpanStyle()
                        .copy(color = Color(0xFF272727))
                ) {
                    append(count)
                }
                withStyle(
                    style = MaterialTheme.typography.bodyMedium.toSpanStyle()
                        .copy(color = Color.Transparent)
                ) {
                    append(offset)
                }
            }
        )
        Divider(
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier
                .padding(start = 2.dp)
                .fillMaxHeight()
                .width(1.dp)
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }
}

data class ParsedLine(
    val countA: String = "",
    val offsetA: String = "",
    val countB: String = "",
    val offsetB: String = "",
    val text: String = ""
)

data class ParsedPart(
    val backgroundColor: Color = Color.Transparent,
    val textColor: Color = Color(0xFFA4A7AC),
    val content: ArrayList<ParsedLine> = ArrayList(),
    val isWrapped: MutableState<Boolean>,
    val wrappedText: String = ""
)
