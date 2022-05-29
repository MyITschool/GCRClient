package com.elseboot3909.gcrclient.ui.change

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.entity.FileInfo
import com.elseboot3909.gcrclient.ui.change.code.FileViewerActivity
import com.elseboot3909.gcrclient.ui.common.LinesChangedCount
import com.elseboot3909.gcrclient.ui.common.changedCountString
import com.elseboot3909.gcrclient.utils.Constants

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun Code(changeInfo: ChangeInfo) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val buttonColor =
        if (isSystemInDarkTheme()) colorResource(R.color.accent_1_700) else colorResource(R.color.neutral_1_100)
    val model: FilesViewModel by (context as ChangeActivity).viewModels()
    val showProgress: Boolean by model.getStatus().observeAsState(false)
    if (showProgress) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp)) {
        val revisions =
            changeInfo.revisions.keys.sortedWith(compareBy { changeInfo.revisions[it]?._number })
        var expandedA by remember { mutableStateOf(false) }
        var expandedB by remember { mutableStateOf(false) }
        var selectedA by remember { mutableStateOf(0) }
        var selectedB by remember { mutableStateOf(revisions.size) }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            ) {
                Button(
                    onClick = { expandedA = true },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.width((screenWidth * 0.4).dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (selectedA == 0) "Base" else "Patchset $selectedA",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = if (expandedA) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                DropdownMenu(
                    expanded = expandedA,
                    onDismissRequest = { expandedA = false },
                    modifier = Modifier
                        .width((screenWidth * 0.4).dp)
                        .background(buttonColor)
                ) {
                    for (i in 0 until selectedB) {
                        if (selectedB > i - 1) {
                            DropdownMenuItem(onClick = {
                                expandedA = false
                                selectedA = i
                            }) {
                                Text(
                                    text = if (i == 0) "Base" else "Patchset $i",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = null,
                modifier = Modifier.size((screenWidth * 0.075).dp)
            )
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            ) {
                Button(
                    onClick = { expandedB = true },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.width((screenWidth * 0.4).dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Patchset $selectedB",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = if (expandedB) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                DropdownMenu(
                    expanded = expandedB,
                    onDismissRequest = { expandedB = false },
                    modifier = Modifier
                        .width((screenWidth * 0.4).dp)
                        .background(buttonColor)
                ) {
                    for (i in selectedA + 1..revisions.size) {
                        DropdownMenuItem(onClick = {
                            expandedB = false
                            selectedB = i
                        }) {
                            Text(
                                text = "Patchset $i",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        val filesList: HashMap<String, FileInfo> by model.getFilesList(
            changeInfo.id,
            revisions[selectedB - 1],
            selectedA
        ).observeAsState(HashMap())
        filesList.remove("/COMMIT_MSG")
        LazyColumn(modifier = Modifier.padding(top = 4.dp)) {
            filesList.keys.forEach { file ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 64.dp)
                            .wrapContentHeight()
                            .padding(top = 4.dp, bottom = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                context.let { ctx ->
                                    val intent = Intent(ctx, FileViewerActivity::class.java)
                                    intent.let {
                                        it.putExtra(Constants.FILE_CHANGE_ID_KEY, changeInfo.id)
                                        it.putExtra(Constants.FILE_PATCHSET_A_KEY, selectedA)
                                        it.putExtra(Constants.FILE_PATCHSET_B_KEY, revisions[selectedB - 1])
                                        it.putExtra(Constants.FILE_NAME_KEY, file)
                                    }
                                    ctx.startActivity(intent)
                                }
                            },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 6.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = file,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.width((screenWidth - 62).dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                LinesChangedCount(
                                    changedCountString(filesList[file]?.lines_inserted ?: 0),
                                    changedCountString(filesList[file]?.lines_deleted ?: 0)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
