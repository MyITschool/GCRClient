@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.vote.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.entity.external.AccountInfo
import com.elseboot3909.gcrclient.entity.external.ApprovalInfo
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.ReviewerInput
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.theme.voteBoxBadColor
import com.elseboot3909.gcrclient.ui.theme.voteBoxBorder
import com.elseboot3909.gcrclient.ui.theme.voteBoxGoodColor
import com.elseboot3909.gcrclient.ui.theme.voteBoxNeutralColor
import com.elseboot3909.gcrclient.ui.vote.Screens
import com.elseboot3909.gcrclient.utils.AccountUtils.Companion.getAvatarById
import com.elseboot3909.gcrclient.utils.AccountUtils.Companion.getShowedName
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
internal fun VoteExtended(
    label: String,
    navController: NavHostController,
    changeInfoRepo: ChangeInfoRepository = get(),
    progressBarRepository: ProgressBarRepository = get()
) {
    val changeInfo: ChangeInfo by changeInfoRepo.changeInfo.collectAsState()
    if (changeInfo.id.isEmpty()) return
    val removableList = changeInfo.removable_reviewers.map { n -> n._account_id }
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as MasterActivity
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Vote") },
                icon = { Icon(imageVector = Icons.Default.HowToVote, contentDescription = null) },
                onClick = {
                    scope.launch {
                        progressBarRepository.acquire()
                        var response =
                            ChangesAPI.getReviewer(changeInfo, AccountInfo(username = "self"))
                        progressBarRepository.release()
                        if (response.status.value in 200..299) {
                            navController.navigate(route = Screens.VoteActions.route)
                        } else {
                            buildAddReviewerDialog(activity) {
                                scope.launch {
                                    progressBarRepository.acquire()
                                    response = ChangesAPI.addReviewer(
                                        changeInfo,
                                        ReviewerInput(reviewer = "self")
                                    )
                                    if (response.status.value in 200..299) {
                                        changeInfoRepo.syncChangeWithRemote()
                                        navController.navigate(route = Screens.VoteActions.route)
                                    } else {
                                        Toast.makeText(activity, "Failed to add!", Toast.LENGTH_SHORT).show()
                                    }
                                    progressBarRepository.release()
                                }
                            }.show()
                        }
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 10.dp)
            ) {
                changeInfo.labels[label]?.let { labelInfo ->
                    voteInfoBox(labelInfo.all.map { n -> n }.filter { n -> n.value > 0 }, voteBoxGoodColor, label, removableList = removableList)
                    voteInfoBox(labelInfo.all.map { n -> n }.filter { n -> n.value < 0 }, voteBoxBadColor, label, removableList = removableList)
                    voteInfoBox(labelInfo.all.map { n -> n }.filter { n -> n.value == 0 }, voteBoxNeutralColor, label, allowRemoving = false)
                }
            }
        }
    }
}


private fun buildAddReviewerDialog(
    context: Context,
    onAdd: () -> Unit
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(context).also {
        it.setTitle("You are not a reviewer!")
        it.setMessage("Currently you are not a reviewer, but you can add yourself to the reviewers")
        it.setNeutralButton("Cancel", null)
        it.setPositiveButton("Add") { _, _ -> onAdd() }
    }
}

@Composable
private fun voteInfoBox(
    labelInfo: List<ApprovalInfo>,
    color: Color,
    label: String,
    allowRemoving: Boolean = true,
    removableList: List<Int> = ArrayList(),
    progressBarRepo: ProgressBarRepository = get(),
    changeInfoRepo: ChangeInfoRepository = get()
) {
    val activity = LocalContext.current as MasterActivity
    val scope = rememberCoroutineScope()
    if (labelInfo.isEmpty()) {
        return
    }
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .border(
                width = ButtonDefaults.OutlinedBorderSize,
                color = voteBoxBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = color)
    ) {
        LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            itemsIndexed(labelInfo) { _, item ->
                val removable = allowRemoving && (item._account_id in removableList)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Row(
                            modifier = Modifier
                                .border(
                                    width = ButtonDefaults.OutlinedBorderSize,
                                    color = voteBoxBorder,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clip(RoundedCornerShape(5.dp))
                                .background(color = Color.White)
                                .padding(start = 12.dp, end = 12.dp)
                                .defaultMinSize(minHeight = 32.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = if (item.avatars.size > 0) item.avatars[item.avatars.size - 1].url else "",
                                error = painterResource(id = getAvatarById(item._account_id)),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(18.dp)
                                    .clip(
                                        CircleShape
                                    )
                            )
                            Text(
                                text = item.username,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = item.value.let { if (it > 0) "+$it" else "$it" },
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .border(
                                    width = ButtonDefaults.OutlinedBorderSize,
                                    color = voteBoxBorder,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clip(RoundedCornerShape(5.dp))
                                .background(color = Color.White)
                                .defaultMinSize(minHeight = 32.dp)
                                .wrapContentHeight()
                                .padding(start = 10.dp, end = 10.dp),
                            color = Color.Black
                        )
                    }
                    if (removable) {
                        IconButton(
                            onClick = {
                                buildRemoveVoteDialog(activity, label, item.value.let { if (it > 0) "+$it" else "$it" }, getShowedName(
                                    AccountInfo(
                                        name = item.name,
                                        username = item.username,
                                        email = item.email,
                                        _account_id = item._account_id
                                    )
                                )
                                ) {
                                    scope.launch {
                                        progressBarRepo.acquire()
                                        val response = ChangesAPI.deleteVote(changeInfoRepo.changeInfo.value, item._account_id, label)
                                        if (response.status.value in 200..299) {
                                            changeInfoRepo.syncChangeWithRemote()
                                        }
                                        progressBarRepo.release()
                                    }
                                }.show()
                            },
                            modifier = Modifier.then(Modifier.size(24.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildRemoveVoteDialog(
    context: Context,
    label: String,
    vote: String,
    username: String,
    onRemove: () -> Unit
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(context).also {
        it.setTitle("Remove this vote?")
        it.setMessage("You are going to remove $vote for $label by $username\nBe careful before removing any votes!")
        it.setNeutralButton("Cancel", null)
        it.setPositiveButton("Remove") { _, _ -> onRemove() }
    }
}