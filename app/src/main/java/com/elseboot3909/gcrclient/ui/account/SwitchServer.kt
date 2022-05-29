package com.elseboot3909.gcrclient.ui.account

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ServerData
import com.elseboot3909.gcrclient.ui.theme.MainTheme
import com.elseboot3909.gcrclient.ui.theme.SetBackground
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.ServerDataManager

@ExperimentalMaterial3Api
class SwitchServer : AppCompatActivity() {

    private val serverList: ArrayList<ServerData> by lazy {
        ServerDataManager.serverDataList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)

        setContent {
            MainTheme {
                SetBackground()
                ServerList()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }

    @Composable
    private fun ServerList() {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val context = LocalContext.current
        Column(
            Modifier
                .fillMaxHeight()
                .padding(top = (screenHeight * 0.45).dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = "Select server",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            val model: FavIconViewModel by viewModels()
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 28.dp, end = 28.dp, bottom = 48.dp, top = 72.dp)
            ) {
                items(items = serverList, itemContent = {
                    Row {
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            onClick = {
                                ServerDataManager.writeNewPosition(context, serverList.indexOf(it))
                                (context as SwitchServer).let {
                                    it.setResult(Constants.ACCOUNT_SWITCHED)
                                    it.finish()
                                }
                            },
                            modifier = Modifier.defaultMinSize(minHeight = 42.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = it.serverURL,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 8.dp)
                                        .requiredWidth((screenWidth - 132).dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val favIcon: String by model.getFavIcon(it.serverURL)!!
                                    .observeAsState(String())
                                AsyncImage(
                                    model = favIcon,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(24.dp)
                                        .clip(
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                        }
                    }
                })
            }
        }
    }
}