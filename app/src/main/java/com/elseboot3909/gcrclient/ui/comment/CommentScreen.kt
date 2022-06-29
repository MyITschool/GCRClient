@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.comment

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.entity.external.CommentInfo
import com.elseboot3909.gcrclient.entity.internal.getAvatar
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.diff.screens.LineCounter
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.DateUtils
import com.elseboot3909.gcrclient.viewmodel.CommentsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
internal fun CommentScreenContent(masterNavCtl: NavController) {
    CommentScreenTopBar(masterNavCtl)
}

@Composable
private fun CommentScreenTopBar(
    masterNavCtl: NavController,
    cViewModel: CommentsViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    val fileName by cViewModel.currentFile.observeAsState("")
    Scaffold(
        topBar = {
            Column(modifier = Modifier.wrapContentHeight()) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = {
                            masterNavCtl.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    backgroundColor = getBackgroundColor()
                )
                Text(
                    text = realFileName(fileName),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = getBackgroundColor())
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                )
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 6.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val fileComments = cViewModel.comments.value?.get(fileName) ?: ArrayList()
                for (comment in fileComments) {
                    if (comment.in_reply_to.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp)
                        ) {
                            CommentsList(comment, fileComments)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CommentsList(
    current: CommentInfo,
    list: ArrayList<CommentInfo>,
    elevation: Int = 0
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, end = 4.dp)) {
            Row(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                AsyncImage(
                    model = getAvatar(current.author.avatars),
                    contentDescription = null,
                    error = painterResource(id = AccountUtils.getAvatarById(current.author._account_id)),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = AccountUtils.getShowedName(current.author),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val date = DateUtils.dateInputFormat.parse(current.updated.replace(".000000000", ""))
                        if (date != null) {
                            Text(
                                text = DateUtils.dateOutputFormat.format(date),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    SelectionContainer {
                        Text(
                            text = current.message,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.wrapContentWidth()
                        )
                    }
                }
            }
            if (elevation == 0 && current.context_lines.isNotEmpty()) {
                Column(modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.Transparent
                    )
                ) {
                    val maxLength = current.context_lines.last().line_number.toString().length
                    val markedLines = ArrayList<Int>()
                    if (current.range.end_line == 0) {
                        markedLines.add(current.line)
                    } else {
                        for (i in current.range.start_line..current.range.end_line) markedLines.add(i)
                    }
                    current.context_lines.forEachIndexed { _, item ->
                        Row(
                            Modifier
                                .height(IntrinsicSize.Min)
                                .background(
                                    color = if (!markedLines.contains(item.line_number)) backgroundColor else Color(
                                        0xFFA8C023
                                    )
                                )
                        ) {
                            Row(modifier = Modifier.background(color = Color(0xFF313335))) {
                                val lineNumber = item.line_number.toString()
                                LineCounter(lineNumber, "0".repeat(maxLength - lineNumber.length))
                            }
                            Row(modifier = Modifier.padding(start = 3.dp)) {
                                Text(
                                    text = item.context_line,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (!markedLines.contains(item.line_number)) Color(0xFFA9B7C6) else backgroundColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
            for (comment in list) {
                if (comment.in_reply_to == current.id) {
                    CommentsList(comment, list, elevation + 8)
                }
            }
        }
    }
}

fun realFileName(fileName: String): String {
    if (fileName == "/PATCHSET_LEVEL") return "Change"
    return fileName
}