@file:OptIn(ExperimentalMaterial3Api::class)

package com.elseboot3909.gcrclient.ui.change.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.ui.common.LinesChangedCount
import com.elseboot3909.gcrclient.ui.common.changedCountString
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.diff.DiffRepository
import com.elseboot3909.gcrclient.repository.diff.FilesRepository
import org.koin.androidx.compose.get

@Composable
internal fun Code(
    masterNavCtl: NavController,
    changeInfoRepo: ChangeInfoRepository = get(),
    diffRepo: DiffRepository = get(),
    filesModelRepo: FilesRepository = get()
) {
    val changeInfo: ChangeInfo by changeInfoRepo.changeInfo.collectAsState()
    if (changeInfo.id.isEmpty()) return
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val revisions =
        changeInfo.revisions.keys.sortedWith(compareBy { changeInfo.revisions[it]?._number })
    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp)) {
        val base by diffRepo.base.collectAsState()
        val revision by diffRepo.revision.collectAsState()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            patchsetSelection(
                onSelect = { i -> diffRepo.base.value = i },
                selected = base,
                from = 0,
                to = revisions.indexOf(revision) + 1
            )
            Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = null,
                modifier = Modifier.size((screenWidth * 0.075).dp)
            )
            patchsetSelection(
                onSelect = { i -> diffRepo.revision.value = revisions[i - 1] },
                selected = revisions.indexOf(revision) + 1,
                from = base + 1,
                to = revisions.size + 1
            )
        }
        CodeFilesList(filesModelRepo, masterNavCtl)
    }
}

@Composable
private fun patchsetSelection(
    onSelect: (Int) -> Unit,
    selected: Int,
    from: Int,
    to: Int
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.width((screenWidth * 0.4).dp),
            colors = ButtonDefaults.buttonColors(containerColor = getBackgroundColor())
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selected == 0) "Base" else "Patchset $selected",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width((screenWidth * 0.4).dp)
                .background(getBackgroundColor())
        ) {
            for (i in from until to) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    onSelect(i)
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


@Composable
private fun CodeFilesList(
    filesModel: FilesRepository,
    masterNavCtl: NavController,
    diffRepo: DiffRepository = get()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val filesList by filesModel.filesList.collectAsState()
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
                            diffRepo.file.value = file
                            masterNavCtl.navigate(route = MasterScreens.DiffScreen.route)
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




