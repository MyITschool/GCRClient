package com.elseboot3909.gcrclient.ui.change

import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.entity.FileInfo

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun Code(changeInfo: ChangeInfo) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val buttonColor =
        if (isSystemInDarkTheme()) colorResource(R.color.accent_1_700) else colorResource(R.color.neutral_1_100)
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
        val model: FilesViewModel by (context as ChangeActivity).viewModels()
        val filesList: HashMap<String, FileInfo> by model.getFilesList(
            changeInfo.id,
            revisions[selectedB - 1],
            selectedA
        ).observeAsState(HashMap())
        filesList.remove("/COMMIT_MSG")
        LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 10.dp)) {
            filesList.keys.forEach {
                item {
                    Card() {
                        Text(text = it)
                    }
                }
            }
        }
    }
}
