package gr.aketh.echoes.composables

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import gr.aketh.echoes.R
import org.json.JSONObject

object  Games {
    @Composable
    fun CardView(title: String, description: String, image: Painter, onButtonClick: () -> Unit) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier.sizeIn(
                minWidth = screenWidth * 0.8f,
                minHeight = screenHeight * 0.9f,
                maxWidth = screenWidth * 0.8f,
                maxHeight = screenHeight * 0.9f
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                    Text(text = title, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 5.dp))
                    Text(text = description, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Justify, modifier = Modifier.fillMaxWidth(0.9f))
                }
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.8f)) {
                    Button(
                        onClick = onButtonClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                    ) {
                        Text("Play", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Carousel(nameAndjsonFiles: List<Pair<String, JSONObject>>, onButtonClick: (String) -> Unit) {
        val listState = rememberLazyListState()
        val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        val landScapeOrientation = LocalConfiguration.current.orientation ==  Configuration.ORIENTATION_LANDSCAPE


        //
        val drawableMap = mapOf(
            "ic_menu_camera" to R.drawable.ic_menu_camera,
            "test" to R.drawable.dxahavtwgnr81,
            "test1" to R.drawable.karagkouna_zografia,
            "test2" to R.drawable.playing_logo
        )
        if(landScapeOrientation)
        {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 16.dp),
                flingBehavior = snapBehavior
            ) {
                items(nameAndjsonFiles) { nameAndjsonFile ->
                    val gameFileName = nameAndjsonFile.first
                    val gameInfo = nameAndjsonFile.second.getJSONObject("game_info")
                    val title = gameInfo.getString("title")
                    val description = gameInfo.getString("description")
                    val imageResource = gameInfo.getString("image")

                    val context = LocalContext.current
                    val packageName = context.packageName

                    val id = drawableMap.getOrElse(imageResource) { R.drawable.ic_menu_camera}

                    CardView(
                        title = title,
                        description = description,
                        image = painterResource(id = id),
                        onButtonClick = { onButtonClick(gameFileName) }
                    )
                }
            }
        }
        else {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp),
                flingBehavior = snapBehavior
            ) {
                items(nameAndjsonFiles) { nameAndjsonFile ->
                    val gameFileName = nameAndjsonFile.first
                    val gameInfo = nameAndjsonFile.second.getJSONObject("game_info")
                    val title = gameInfo.getString("title")
                    val description = gameInfo.getString("description")
                    val imageResource = gameInfo.getString("image")

                    val context = LocalContext.current
                    val packageName = context.packageName

                    val id = drawableMap.getOrElse(imageResource) { R.drawable.ic_menu_camera }

                    CardView(
                        title = title,
                        description = description,
                        image = painterResource(id = id),
                        onButtonClick = { onButtonClick(gameFileName) }
                    )
                }
            }
        }


    }

    @Composable
    fun Test(navController: NavHostController) {
        Text("Your dad")
    }
}