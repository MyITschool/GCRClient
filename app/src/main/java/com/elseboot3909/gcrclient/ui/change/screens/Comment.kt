@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.change.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.repository.CommentsRepository
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.viewmodel.CommentsViewModel
import com.elseboot3909.gcrclient.viewmodel.ChangeInfoViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun Comment(
    masterNavCtl: NavController,
    ciViewModel: ChangeInfoViewModel = getViewModel(owner = LocalContext.current as MasterActivity),
    cViewModel: CommentsViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    val changeInfo: ChangeInfo by ciViewModel.changeInfo.observeAsState(ChangeInfo())
    if (changeInfo.id.isEmpty()) return
    val commentsMap by cViewModel.comments.observeAsState(HashMap())
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
    cRepo: CommentsRepository = get()
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
                            cRepo.currentFile.value = file
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
