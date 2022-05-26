package com.elseboot3909.gcrclient.ui.change.vote

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ApprovalInfo
import com.elseboot3909.gcrclient.entity.LabelInfo
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.utils.AccountUtils

@ExperimentalMaterial3Api
class VoteDetailsActivity : AppCompatActivity() {

    private val labelInfo: LabelInfo by lazy {
        intent.extras?.getSerializable("labelInfo") as LabelInfo
    }

    private val label: String by lazy {
        intent.extras?.getString("label") as String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)

        setContent {
            MainTheme {
                VoteDetails()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }

    @Composable
    private fun VoteDetails() {
        val backgroundColor =
            if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
        Scaffold(
            topBar = {
                Column(modifier = Modifier.wrapContentHeight()) {
                    TopAppBar(
                        title = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        backgroundColor = backgroundColor
                    )
                }
            }
        ) {
            labelInfo.all.map { n -> n }.filter { n -> n.value > 0 }
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 10.dp)) {
                    voteInfoBox(labelInfo.all.map { n -> n }.filter { n -> n.value > 0 }, Color(0xFFE3F7EA))
                    voteInfoBox(labelInfo.all.map { n -> n }.filter { n -> n.value < 0 }, Color(0xFFFFB0B0))
                    voteInfoBox(labelInfo.all.map { n -> n }.filter { n -> n.value == 0 }, colorResource(R.color.neutral_1_200))
                }
            }
        }
    }
}

@Composable
private fun voteInfoBox(labelInfo: List<ApprovalInfo>, color: Color) {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .border(
                width = ButtonDefaults.OutlinedBorderSize,
                color = Color(0xFF79747E),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = color)
    ) {
        LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            itemsIndexed(labelInfo) { index, item ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .border(
                                width = ButtonDefaults.OutlinedBorderSize,
                                color = Color(0xFF79747E),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clip(RoundedCornerShape(5.dp))
                            .background(color = Color.White)
                            .padding(start = 12.dp, end = 12.dp)
                            .defaultMinSize(minHeight = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = if (item.avatars.size > 0) item.avatars[item.avatars.size - 1].url else "",
                            error = painterResource(id = AccountUtils.dummyAvatars[index % AccountUtils.dummyAvatars.size]),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(18.dp)
                                .clip(
                                    CircleShape
                                )
                        )
                        Text(
                            text = item.username,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = item.value.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .border(
                                width = ButtonDefaults.OutlinedBorderSize,
                                color = Color(0xFF79747E),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clip(RoundedCornerShape(5.dp))
                            .background(color = Color.White)
                            .defaultMinSize(minHeight = 32.dp)
                            .wrapContentHeight()
                            .padding(start = 10.dp, end = 10.dp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}
