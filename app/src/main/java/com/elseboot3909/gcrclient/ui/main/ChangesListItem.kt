package com.elseboot3909.gcrclient.ui.main

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.ui.change.ChangeActivity
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.DateUtils
import java.io.Serializable
import java.text.ParseException

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun ChangesListItem(index: Int, item: ChangeInfo) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                val intent =
                    Intent(context as MainActivity, ChangeActivity::class.java)
                intent.putExtra(
                    "changeInfo",
                    item as Serializable
                )
                context.startActivity(intent)
            }
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
                model = if (item.owner.avatars.size > 0) item.owner.avatars[item.owner.avatars.size - 1].url else "",
                error = painterResource(id = AccountUtils.dummyAvatars[index % AccountUtils.dummyAvatars.size]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(
                        CircleShape
                    )
            )
            Text(
                text = item.owner.username,
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
                text = dateToString(item.updated),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_insertions_background),
                    contentDescription = null
                )
                Text(
                    text = changedCountString(item.insertions),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Box {
                Image(
                    painter = painterResource(id = R.drawable.ic_deletions_background),
                    contentDescription = null
                )
                Text(
                    text = changedCountString(item.deletions),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

private fun changedCountString(value: Int): String {
    return if (value > 999) "999+" else value.toString()
}

private fun dateToString(value: String): String {
    try {
        val date = DateUtils.dateInputFormat.parse(value.replace(".000000000", ""))
        if (date != null) {
            return when {
                DateUtils.monthOutputFormat.format(date) >= DateUtils.monthOutputFormat.format(
                    DateUtils.currentData
                ) -> {
                    DateUtils.clockOutputFormat.format(date)
                }
                DateUtils.yearOutputFormat.format(date) >= DateUtils.yearOutputFormat.format(
                    DateUtils.currentData
                ) -> {
                    DateUtils.monthOutputFormat.format(date)
                }
                else -> {
                    DateUtils.yearOutputFormat.format(date)
                }
            }
        }
    } catch (ignored: ParseException) {
    }
    return ""
}