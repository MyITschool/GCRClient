@file:OptIn(ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.diff.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.repository.diff.DiffRepository
import com.elseboot3909.gcrclient.repository.diff.FileDiffRepository
import org.koin.androidx.compose.get
import kotlin.math.max

@Composable
internal fun CompareLines(
    fileDiffViewModel: FileDiffRepository = get(),
    diffRepo: DiffRepository = get()
) {
    val diffInfo = fileDiffViewModel.diffInfo.collectAsState()
    val maxSize = max(diffInfo.value.meta_a.lines, diffInfo.value.meta_b.lines).toString().length
    val parsedContent = ArrayList<ParsedPart>()
    var offsetA = 0
    var offsetB = 0
    diffInfo.value.content.forEachIndexed { part, content ->
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
                } else if (i >= content.ab.size - 10 && diffInfo.value.content.size - 1 != part) {
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
                    backgroundColor = Color(0xFF2B2B2B),
                    content = parsedLinesBefore,
                    isWrapped = remember { mutableStateOf(false) })
            )
        }
        if (parsedLinesWrapped.isNotEmpty()) {
            parsedContent.add(
                ParsedPart(
                    backgroundColor = Color(0xFF2B2B2B),
                    content = parsedLinesWrapped,
                    isWrapped = remember { mutableStateOf(true) },
                    wrappedText = "Expand ${parsedLinesWrapped.size} lines"
                )
            )
        }
        if (parsedLinesAfter.isNotEmpty()) {
            parsedContent.add(
                ParsedPart(
                    backgroundColor = Color(0xFF2B2B2B),
                    content = parsedLinesAfter,
                    isWrapped = remember { mutableStateOf(false) })
            )
        }
        if (parsedLinesA.isNotEmpty()) {
            parsedContent.add(
                ParsedPart(
                    backgroundColor = Color(0xFFFFB0B0),
                    content = parsedLinesA,
                    isWrapped = remember { mutableStateOf(false) })
            )
        }
        if (parsedLinesB.isNotEmpty()) {
            parsedContent.add(
                ParsedPart(
                    backgroundColor = Color(0xFF61936F),
                    content = parsedLinesB,
                    isWrapped = remember { mutableStateOf(false) })
            )
        }
    }
    val fileName = diffRepo.file.collectAsState()
    Scaffold(
        topBar = {
            LineText(
                ParsedLine("0", "0".repeat(maxSize - 1), "0", "0".repeat(maxSize - 1), fileName.value),
                Color(0xFF2B2B2B),
                Color(0xFFA9B7C6)
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
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
    }
}

@Composable
private fun LineText(parsedLine: ParsedLine, backgroundColor: Color, textColor: Color) {
    Row(
        Modifier
            .height(IntrinsicSize.Min)
            .background(color = backgroundColor)
    ) {
        Row(modifier = Modifier.background(color = Color(0xFF313335))) {
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
fun LineCounter(count: String, offset: String) {
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
                    .copy(color = Color(0xFFA4A3A3))
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
        color = Color(0xFF555555),
        modifier = Modifier
            .padding(start = 2.dp)
            .fillMaxHeight()
            .width(1.dp)
    )
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
    val textColor: Color = Color(0xFFA9B7C6),
    val content: ArrayList<ParsedLine> = ArrayList(),
    val isWrapped: MutableState<Boolean>,
    val wrappedText: String = ""
)
