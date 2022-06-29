@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.change.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults.OutlinedBorderSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.viewmodel.ChangeInfoViewModel
import org.koin.androidx.compose.getViewModel
import kotlin.math.abs

@Composable
internal fun Vote(
    masterNavCtl: NavController,
    ciViewModel: ChangeInfoViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    val changeInfo: ChangeInfo by ciViewModel.changeInfo.observeAsState(ChangeInfo())
    val labels = changeInfo.labels
    val labelList = ArrayList<LabelPreview>()
    for (label in labels.keys) {
        val approvalList = labels[label]?.all
        var min = 0
        var max = 0
        if (approvalList != null) {
            for (approvalInfo in approvalList) {
                if (approvalInfo.value > 0) max += approvalInfo.value else min += approvalInfo.value
            }
        }
        min = abs(min)
        labelList.add(
            LabelPreview(
                label = label,
                min = min,
                max = max,
                rejected = labels[label]?.rejected?._account_id != 0,
                approved = labels[label]?.approved?._account_id != 0,
                recommended = labels[label]?.recommended?._account_id != 0,
                disliked = labels[label]?.disliked?._account_id != 0
            )
        )
    }
    Column {
        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp, start = 8.dp),
            text = "Labels", style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        LazyColumn {
            items(items = labelList, itemContent = {
                Row(
                    modifier = Modifier
                        .padding(
                            start = 10.dp,
                            end = 10.dp,
                            top = 6.dp,
                            bottom = 6.dp
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val labelIcon = when {
                            it.rejected || (it.disliked && !(it.approved || it.recommended)) -> Icons.Default.Close
                            it.approved && !it.rejected || it.approved && !it.disliked -> Icons.Default.Done
                            it.disliked && it.recommended -> Icons.Default.HourglassFull
                            else -> Icons.Default.Schedule
                        }
                        val iconColor = when {
                            it.rejected || (it.disliked && !(it.approved || it.recommended)) -> Color(0xFFFA3232)
                            it.approved && !it.rejected || it.approved && !it.disliked -> Color(0xFF06A36A)
                            else -> Color(0xFFDE7411)
                        }
                        Icon(
                            imageVector = labelIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(20.dp),
                            tint = iconColor
                        )
                        Text(
                            text = it.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (it.min == 0 && it.max == 0) {
                            Text(
                                text = "No votes",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        } else {
                            for (value in listOf(-it.min, it.max)) {
                                if (value != 0) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .border(
                                                color = if (value < 0) Color(0xFFFF7272) else Color(
                                                    0xFF68D593
                                                ),
                                                width = OutlinedBorderSize,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .background(
                                                color = if (value < 0) Color(0xFFFFB0B0) else Color(
                                                    0xFFE3F7EA
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(
                                                start = 8.dp,
                                                end = 8.dp,
                                                bottom = 1.5.dp,
                                                top = 1.5.dp
                                            ),
                                        text = if (value > 0) "+${value}" else "$value",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black,
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                masterNavCtl.navigate(route = "${MasterScreens.VoteScreen.route}/${it.label}")
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .padding(start = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    }
                }
            })
        }
    }
}

data class LabelPreview(
    val label: String = "",
    val min: Int = 0,
    val max: Int = 0,
    val rejected: Boolean = false,
    val approved: Boolean = false,
    val recommended: Boolean = false,
    val disliked: Boolean = false
)