@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.vote.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.ReviewInput
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
internal fun VoteActions(
    label: String,
    navController: NavHostController,
    changeInfoRepo: ChangeInfoRepository = get(),
    progressBarRepository: ProgressBarRepository = get()
) {
    val changeInfo: ChangeInfo by changeInfoRepo.changeInfo.collectAsState()
    if (changeInfo.id.isEmpty()) return
    val listState = rememberLazyListState()
    val activity = LocalContext.current as MasterActivity
    val scope = rememberCoroutineScope()
    val values = changeInfo.permitted_labels[label] ?: ArrayList()
    val fakeString = if (values.size != 0) "${"+".repeat(values.maxOf { n -> n.length })} - " else ""
    val visibleCount = values.size.let { if (it <= 0) 1 else if (it <= 5) it else 5 }
    val selectionButtonWidth = (LocalConfiguration.current.screenWidthDp / visibleCount).dp
    Scaffold(
        floatingActionButton = {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(
                        visible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index != listState.layoutInfo.totalItemsCount - 1,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Text(
                            text = "swipe to see full list ->",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 3.dp, end = 3.dp, bottom = 3.dp)
                        )
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    state = listState
                ) {
                    items(
                        count = values.size,
                        key = { values[it] },
                        itemContent = {
                            FilledTonalButton(
                                modifier = Modifier
                                    .padding(start = 2.dp, end = 2.dp)
                                    .defaultMinSize(
                                        minWidth = selectionButtonWidth,
                                        minHeight = 56.dp
                                    ),
                                onClick = {
                                    buildConfirmationDialog(activity, label, values[it].trim()) {
                                        scope.launch {
                                            progressBarRepository.acquire()
                                            val response = ChangesAPI.setReview(changeInfo, ReviewInput(hashMapOf(label to values[it])))
                                            if (response.status.value in 200..299) {
                                                changeInfoRepo.syncChangeWithRemote()
                                                navController.popBackStack()
                                            } else {
                                                Toast.makeText(
                                                    activity,
                                                    "Failed to vote!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            progressBarRepository.release()
                                        }
                                    }.show()
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = values[it].trim())
                            }
                        }
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = "Rules",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Column(modifier = Modifier
                    .padding(top = 12.dp, bottom = 16.dp)
                    .border(
                        width = ButtonDefaults.OutlinedBorderSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ButtonDefaults.OutlinedBorderOpacity),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(top = 10.dp, bottom = 2.dp, start = 10.dp, end = 8.dp)
                ) {
                    for (value in values) {
                        Row(modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .padding(bottom = 8.dp)
                        ) {
                            Box(modifier = Modifier.wrapContentWidth()) {
                                Text(
                                    text = "${value.trim()} - ",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                                Text(
                                    text = fakeString,
                                    style = MaterialTheme.typography.labelLarge.copy(color = Color.Transparent),
                                )
                            }
                            Text(
                                text = changeInfo.labels[label]?.values?.get(value) ?: "",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildConfirmationDialog(
    context: Context,
    label: String,
    value: String,
    onVote: () -> Unit
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(context).also {
        it.setTitle("You sure want to vote?")
        it.setMessage("$value for $label\nYou can change your vote on this label later")
        it.setNeutralButton("Cancel", null)
        it.setPositiveButton("Vote") { _, _ -> onVote() }
    }
}
