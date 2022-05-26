package com.elseboot3909.gcrclient.ui.change

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults.OutlinedBorderOpacity
import androidx.compose.material.ButtonDefaults.OutlinedBorderSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.DateUtils

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun Info(changeInfo: ChangeInfo) {
    val context = LocalContext.current

    if (changeInfo.id.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
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
                                    error = painterResource(id = AccountUtils.dummyAvatars[infoList.indexOf(info) % AccountUtils.dummyAvatars.size]),
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
            Text(
                text = changeInfo.revisions[changeInfo.current_revision]?.commit?.message ?: "",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    .border(
                        width = OutlinedBorderSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = OutlinedBorderOpacity),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(top = 10.dp, bottom = 2.dp, start = 10.dp, end = 8.dp)
            )
        }
    }

    BackHandler(true) {
        (context as ChangeActivity).finish()
    }
}

data class InfoData(
    val label: String = "",
    val str: String = "",
    val requiresAvatar: Boolean = false,
    val url: String = ""
)