@file:OptIn(ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.change.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.viewmodel.comments.CommentsViewModel
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import org.koin.androidx.compose.get

@Composable
internal fun Comment(
    masterNavCtl: NavController,
    changeInfoRepo: ChangeInfoRepository = get(),
    commentsViewModel: CommentsViewModel = get()
) {
    val changeInfo: ChangeInfo by changeInfoRepo.changeInfo.collectAsState()
    if (changeInfo.id.isEmpty()) return
    val commentsMap by commentsViewModel.comments.observeAsState(HashMap())
    if (commentsMap.keys.isNotEmpty()) {
        CommentedFiles(commentsMap.keys, masterNavCtl)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "No comments yet",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CommentedFiles(
    files: MutableSet<String>,
    masterNavCtl: NavController,
    commentsViewModel: CommentsViewModel = get()
) {
    LazyColumn(modifier = Modifier.padding(top = 4.dp)) {
        files.forEach { file ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 42.dp)
                        .padding(top = 4.dp, bottom = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            commentsViewModel.currentFile.value = file
                            masterNavCtl.navigate(route = MasterScreens.CommentScreen.route)
                        },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 6.dp, top = 4.dp, bottom = 4.dp)
                            .defaultMinSize(minHeight = 42.dp)
                    ) {
                        Text(
                            text = if (file == "/PATCHSET_LEVEL") "Change" else file,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
