@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.vote.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.elseboot3909.gcrclient.repository.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.ProgressBarRepository
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.ChangeInfoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object VoteActions : KoinComponent {
    @Composable
    internal fun VoteActions(
        label: String,
        navController: NavHostController,
        ciViewModel: ChangeInfoViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
    ) {
        val changeInfo: ChangeInfo by ciViewModel.changeInfo.observeAsState(ChangeInfo())
        if (changeInfo.id.isEmpty()) return
        val listState = rememberLazyListState()
        val activity = LocalContext.current as MasterActivity
        val scope = rememberCoroutineScope()
        val values = changeInfo.permitted_labels[label] ?: ArrayList()
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
                                        buildConfirmationDialog(
                                            activity,
                                            label,
                                            values[it].trim()
                                        ) {
                                            scope.launch {
                                                voteOnLabel(
                                                    activity,
                                                    changeInfo,
                                                    ReviewInput(hashMapOf(label to values[it])),
                                                    navController
                                                )
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
                VoteActionsContent(changeInfo, values, label)
            }
        }
    }

    @Composable
    private fun VoteActionsContent(
        changeInfo: ChangeInfo,
        labels: ArrayList<String>,
        labelName: String
    ) {
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
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 16.dp)
                    .border(
                        width = ButtonDefaults.OutlinedBorderSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ButtonDefaults.OutlinedBorderOpacity),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(top = 10.dp, bottom = 2.dp, start = 10.dp, end = 8.dp)
            ) {
                for (label in labels) {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .padding(bottom = 8.dp)
                    ) {
                        Box(modifier = Modifier.wrapContentWidth()) {
                            Text(
                                text = "${label.trim()} - ",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                            Text(
                                text = if (labels.size != 0) "${"+".repeat(labels.maxOf { n -> n.length })} - " else "",
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.Transparent),
                            )
                        }
                        Text(
                            text = changeInfo.labels[labelName]?.values?.get(label) ?: "",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }

    /**
     * This function build a dialog to confirm voting on label
     *
     * @param context context of application
     * @param label label, that user expects to vote on
     * @param value value of user's vote
     * @param onVote function which will start after positive decision
     */
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

    /**
     * This function handle a request to vote.
     */
    private suspend fun voteOnLabel(
        context: Context,
        changeInfo: ChangeInfo,
        reviewInput: ReviewInput,
        navController: NavHostController
    ) {
        val pbRepo: ProgressBarRepository by inject()
        val ciRepo: ChangeInfoRepository by inject()
        pbRepo.acquire()
        val response = ChangesAPI.setReview(changeInfo, reviewInput)
        if (response.status.value in 200..299) {
            ciRepo.syncChangeWithRemote()
            navController.popBackStack()
        } else {
            Toast.makeText(context, "Failed to vote!", Toast.LENGTH_SHORT).show()
        }
        pbRepo.release()
    }
}