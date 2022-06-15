@file:OptIn(ExperimentalMaterialApi::class)

package com.elseboot3909.gcrclient.ui.change.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ButtonDefaults.OutlinedBorderOpacity
import androidx.compose.material.ButtonDefaults.OutlinedBorderSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.CommitMessageInput
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.DateUtils
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
internal fun Info(
    changeInfoRepo: ChangeInfoRepository = get(),
    progressBarRepo: ProgressBarRepository = get()
) {
    val changeInfo: ChangeInfo by changeInfoRepo.changeInfo.collectAsState()
    if (changeInfo.id.isEmpty()) return
    val scope = rememberCoroutineScope()
    val bottomSheet = rememberBottomSheetScaffoldState()
    BackHandler(bottomSheet.bottomSheetState.isExpanded) {
        scope.launch {
            bottomSheet.bottomSheetState.collapse()
        }
    }
    val commitMsg = changeInfo.revisions[changeInfo.current_revision]?.commit?.message ?: ""
    var commitMsgEditable by remember { mutableStateOf(commitMsg) }
    BottomSheetScaffold(
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                BasicTextField(
                    value = commitMsgEditable,
                    onValueChange = { commitMsgEditable = it },
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 76.dp, start = 12.dp, end = 12.dp)
                        .border(
                            width = OutlinedBorderSize,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = OutlinedBorderOpacity),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(top = 9.dp, bottom = 9.dp, start = 10.dp, end = 8.dp),
                    textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
                )
                ExtendedFloatingActionButton(
                    text = { Text(text = "Save") },
                    icon = { Icon(imageVector = Icons.Default.Save, contentDescription = null) },
                    onClick = {
                        scope.launch {
                            progressBarRepo.acquire()
                            bottomSheet.bottomSheetState.collapse()
                            val response = ChangesAPI.setCommitMessage(changeInfo, CommitMessageInput(commitMsgEditable))
                            if (response.status.value in 200..299) {
                                changeInfoRepo.syncChangeWithRemote()
                            }
                            progressBarRepo.release()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                )
            }
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = bottomSheet,
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        contentColor = MaterialTheme.colorScheme.surface,
        sheetBackgroundColor = getBackgroundColor(),
        backgroundColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                scope.launch {
                    bottomSheet.bottomSheetState.let {
                        if (it.isExpanded) it.collapse()
                    }
                }
            })
        }
    ) {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            val date = DateUtils.dateInputFormat.parse(changeInfo.updated.replace(".000000000", ""))
            if (date != null) Text(
                text = DateUtils.dateOutputFormat.format(date),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            val infoList = ArrayList<InfoData>()
            if (changeInfo.owner.name.isNotEmpty()) infoList.add(
                InfoData(
                    label = "Owner",
                    str = changeInfo.owner.name,
                    requiresAvatar = true,
                    url = if (changeInfo.owner.avatars.size != 0) changeInfo.owner.avatars[changeInfo.owner.avatars.size - 1].url else ""
                )
            )
            val reviewers = changeInfo.reviewers
            if (reviewers.containsKey("REVIEWER")) {
                val subArray = reviewers["REVIEWER"]
                if (subArray != null && subArray.size > 0) {
                    infoList.add(
                        InfoData(
                            label = "Reviewer",
                            str = subArray[0].name,
                            requiresAvatar = true,
                            url = if (subArray[0].avatars.size != 0) subArray[0].avatars[subArray[0].avatars.size - 1].url else ""
                        )
                    )
                }
            }
            if (reviewers.containsKey("CC")) {
                val subArray = reviewers["CC"]
                if (subArray != null && subArray.size > 0) {
                    infoList.add(
                        InfoData(
                            label = "CC",
                            str = subArray[0].name,
                            requiresAvatar = true,
                            url = if (subArray[0].avatars.size != 0) subArray[0].avatars[subArray[0].avatars.size - 1].url else ""
                        )
                    )
                }
            }
            if (changeInfo.project.isNotEmpty()) infoList.add(
                InfoData(
                    label = "Project",
                    str = changeInfo.project
                )
            )
            if (changeInfo.branch.isNotEmpty()) infoList.add(InfoData("Branch", changeInfo.branch))
            if (changeInfo.topic.isNotEmpty()) infoList.add(InfoData("Topic", changeInfo.topic))
            for (info in infoList) {
                Row(
                    modifier = Modifier.defaultMinSize(minHeight = 42.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = info.label, style = MaterialTheme.typography.titleMedium)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 24.dp)
                                .border(
                                    width = OutlinedBorderSize,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = OutlinedBorderOpacity),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(start = 12.dp, end = 12.dp)
                                .defaultMinSize(minHeight = 32.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (info.requiresAvatar) {
                                AsyncImage(
                                    model = info.url,
                                    error = painterResource(
                                        id = AccountUtils.dummyAvatars[infoList.indexOf(
                                            info
                                        ) % AccountUtils.dummyAvatars.size]
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(18.dp)
                                        .clip(
                                            CircleShape
                                        )
                                )
                            }
                            Text(
                                text = info.str,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .border(
                        width = OutlinedBorderSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = OutlinedBorderOpacity),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(top = 10.dp, bottom = 6.dp, start = 10.dp, end = 8.dp)
                    .animateContentSize(animationSpec = tween(durationMillis = 500))
            ) {
                Text(
                    text = changeInfo.revisions[changeInfo.current_revision]?.commit?.message ?: "",
                    style = MaterialTheme.typography.labelLarge
                )
                if (get<ServerData>().username == changeInfo.owner.username) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Edit") },
                        icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) },
                        onClick = { scope.launch {
                            bottomSheet.bottomSheetState.expand()
                        } },
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
        }
    }
}

data class InfoData(
    val label: String = "",
    val str: String = "",
    val requiresAvatar: Boolean = false,
    val url: String = ""
)