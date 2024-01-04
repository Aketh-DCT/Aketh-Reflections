package gr.aketh.echoes.composables

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import gr.aketh.echoes.R
import org.json.JSONException
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
                minHeight = screenHeight * 0.8f,
                maxWidth = screenWidth * 0.8f,
                maxHeight = screenHeight * 0.8f
            )//,
          //  colors = CardDefaults.cardColors(
                //containerColor = MaterialTheme.colorScheme.surface // Set the background color here
          //  )
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
                    Text(text = title, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 5.dp))
                    Text(text = description,color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Justify, modifier = Modifier.fillMaxWidth(0.9f))
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
    fun Carousel(nameAndjsonFiles: List<Pair<String, JSONObject>>, onButtonClick: (Array<String>) -> Unit, tooltipText: String) {
        val listState = rememberLazyListState()
        val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        val landScapeOrientation = LocalConfiguration.current.orientation ==  Configuration.ORIENTATION_LANDSCAPE


        //
        val drawableMap = mapOf(
            "ic_menu_camera" to R.drawable.ic_menu_camera,
            "test" to R.drawable.dxahavtwgnr81,
            "test1" to R.drawable.karagkouna_zografia,
            "test2" to R.drawable.playing_logo,
            "iasi_featured_image" to R.drawable.iasi_featured_image,
            "trikala_featured_image" to R.drawable.trikala_featured_image,
            "foligno_featured_image" to R.drawable.foligno_featured_image,
            "istanbul_featured_img" to R.drawable.istanbul_featured_img
        )
        if(landScapeOrientation)
        {
            Text(
                text = tooltipText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
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
                    var introText: String
                    try {
                        //Check if there is text inside this variable in the json , if not who cares
                        introText = gameInfo.getString("intro_text")
                    }catch (_:JSONException){
                        introText = "No text found."
                    }




                    val context = LocalContext.current
                    val packageName = context.packageName

                    val id = drawableMap.getOrElse(imageResource) { R.drawable.ic_menu_camera }

                    var gameFileInfo = arrayOf(gameFileName, introText, id.toString())

                    CardView(
                        title = title,
                        description = description,
                        image = painterResource(id = id),
                        onButtonClick = { onButtonClick(gameFileInfo) }
                    )
                }
            }
        }
        else {

            var expanded by remember { mutableStateOf(false) }

            val dividerWidth by animateDpAsState(
                targetValue = if (expanded) 0.3f.dp else 0.dp, label = "",
                animationSpec = tween(durationMillis = 2000)
            )

            LaunchedEffect(Unit) {
                expanded = true
            }

            Column(
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = tooltipText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
                )
                Divider(color = Color.Black, thickness = 5.dp, modifier = Modifier.fillMaxWidth(dividerWidth.value)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 7.0.dp, bottom = 2.0.dp)
                    .clip(RoundedCornerShape(5.dp)))
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

                        var introText: String
                        var introSound: String

                        //TODO could improve here and create a function to do this
                        try {
                            //Check if there is text inside this variable in the json , if not who cares
                            introText = gameInfo.getString("intro_text")
                        }catch (_:JSONException){
                            introText = "No text found."
                        }

                        try {
                            //Check if there is text inside this variable in the json , if not who cares
                            introSound = gameInfo.getString("intro_sound")
                        }catch (_:JSONException){
                            introSound = "eng_intro"
                        }

                        val id = drawableMap.getOrElse(imageResource) { R.drawable.ic_menu_camera }

                        var gameFileInfo = arrayOf(gameFileName, introText, id.toString(), title, introSound)



                        CardView(
                            title = title,
                            description = description,
                            image = painterResource(id = id),
                            onButtonClick = { onButtonClick(gameFileInfo) }
                        )
                    }
                }
            }

        }


    }

    @Composable
    fun Test(navController: NavHostController) {
        Text("Your dad")
    }


}