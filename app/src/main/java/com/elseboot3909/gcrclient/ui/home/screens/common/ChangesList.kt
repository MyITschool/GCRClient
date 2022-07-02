package com.elseboot3909.gcrclient.ui.home.screens.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.entity.internal.ChangeInfoPreview
import com.elseboot3909.gcrclient.repository.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.SearchParamsRepository
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.ui.common.LinesChangedCount
import com.elseboot3909.gcrclient.utils.Constants
import org.koin.androidx.compose.get

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun ChangesList(
    changesList: List<ChangeInfoPreview>,
    listState: LazyListState,
    masterNavCtl: NavHostController,
    requiresPadding: Boolean = false,
    ciRepo: ChangeInfoRepository = get(),
    searchRepo: SearchParamsRepository = get()
) {
    val descriptionSize = (LocalConfiguration.current.screenWidthDp - 114).dp
    LazyColumn(
        state = listState
    ) {
        items(
            count = changesList.size,
            key = { changesList[it].id },
            itemContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            ciRepo.changeInfo.value = changesList[it].changeInfo
                            ciRepo.syncChangeWithRemote()
                            masterNavCtl.navigate(MasterScreens.ChangeScreen.route)
                        }
                        .padding(bottom = 6.dp, top = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(end = 10.dp, start = 8.dp)
                            .wrapContentHeight()
                            .width(46.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = changesList[it].avatarUrl,
                            contentDescription = null,
                            error = painterResource(id = changesList[it].dummyAvatar),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = changesList[it].showedName,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.width(descriptionSize),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = changesList[it].subject,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = changesList[it].project,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = changesList[it].branch,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = changesList[it].time,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                        LinesChangedCount(changesList[it].insertions, changesList[it].deletions)
                    }
                }
            }
        )
        if (requiresPadding) {
            item {
                val offset by searchRepo.offset.collectAsState()
                if (offset != 0 || changesList.size >= Constants.MAX_FETCHED_CHANGES) {
                    Divider(
                        thickness = 68.dp,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    )
                }
            }
        }
    }
}
