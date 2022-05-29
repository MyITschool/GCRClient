package com.elseboot3909.gcrclient.ui.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.entity.ChangeInfoPreview

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun ChangesListItem(item: ChangeInfoPreview, onClick: () -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(bottom = 4.dp, top = 4.dp)
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
                model = item.avatarUrl,
                error = painterResource(id = item.dummyAvatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(
                        CircleShape
                    )
            )
            Text(
                text = item.showedName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Column(
            modifier = Modifier.width((screenWidth - 114).dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.subject,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = item.project,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = item.branch,
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
                text = item.time,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            LinesChangedCount(item.insertions, item.deletions)
        }
    }
}
