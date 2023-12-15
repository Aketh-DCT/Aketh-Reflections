package gr.aketh.echoes

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.cardview.widget.CardView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gr.aketh.echoes.composables.ContactUs
import gr.aketh.echoes.composables.Games
import gr.aketh.echoes.composables.Language
import gr.aketh.echoes.composables.Screen
import gr.aketh.echoes.ui.theme.Echoes_AkethTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class Start : ComponentActivity() {
    var nameAndjsonFiles: List<Pair<String, JSONObject>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            nameAndjsonFiles = loadJsonFilesFromAssets()

            //Set content is default. It's a lambda function
            setContent {
                Box(modifier = Modifier
                    .fillMaxSize()
                ) {
                    Games.Carousel(
                        nameAndjsonFiles = nameAndjsonFiles!!,
                        onButtonClick = {
                            jsonFile->
                                val intent= Intent(applicationContext, GameInfoActivity::class.java)
                                intent.putExtra("jsonFile", jsonFile)

                                startActivity(intent)
                        }
                    )
                }

                AppNavigation(nameAndjsonFiles = nameAndjsonFiles,onButtonClick = {
                        jsonFile->
                    val intent= Intent(applicationContext, GameInfoActivity::class.java)
                    intent.putExtra("jsonFile", jsonFile)

                    startActivity(intent)
                })

            }
        }
    }
    private suspend fun loadJsonFilesFromAssets(): List<Pair<String,JSONObject>> {
        val jsonFiles = mutableListOf<JSONObject>()
        val nameAndjsonFiles = mutableListOf<Pair<String,JSONObject>>()


            withContext(Dispatchers.IO) {
                val files = assets.list("Content") ?: emptyArray()


                //Find all files in folder Content
                for (filename in files) {
                    if (filename.endsWith(".json")) {
                        val inputStream: InputStream = assets.open("Content/$filename")
                        val size: Int = inputStream.available()
                        val buffer = ByteArray(size)
                        inputStream.read(buffer)
                        inputStream.close()
                        val json = String(buffer, Charset.defaultCharset())
                        val jsonObject = JSONObject(json)
                        nameAndjsonFiles.add(Pair(filename,jsonObject))
                    }
                }
            }
        return nameAndjsonFiles
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Echoes_AkethTheme {
        Greeting("Android")
    }
}

 */

/*
    This is a composable... You may be asking "Marios, what is a composable?"
    Why use it? Well buckle up kiddo(or old timer) I don't judge.
    Well, long time ago, you would use xml as layout
    The problem? Not efficient to have dynamic layouts since you can not add them on runtime
    But you may say, Marios... I don't understand what is all that jargon, and you're right
    I need to fill my comment quota for the year, so I am rambling.

    What you need to know, it that this is somewhat of layout with code, simple to use,
    Just don't touch much, because things may brake, unless you know what you do!
 */


@Composable
fun AppNavigation(nameAndjsonFiles: List<Pair<String, JSONObject>>?, onButtonClick: (String) -> Unit)
{
    val navController = rememberNavController()
    val currentRoute by remember {
        mutableStateOf(Screen.Games.route)
    }

    val items = listOf(
        Screen.Games,
        Screen.Language,
        Screen.ContactUs
    )

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                //val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
            innerPadding ->
        NavHost(navController, startDestination = Screen.Games.route, Modifier.padding(innerPadding)) {
            composable(Screen.Games.route) {
                if (nameAndjsonFiles != null) {
                    Games.Carousel(
                        nameAndjsonFiles = nameAndjsonFiles!!,
                        onButtonClick = onButtonClick
                    )
                }
            }
            composable(Screen.Language.route) { Language.test(navController) }
            composable(Screen.ContactUs.route) { ContactUs.test(navController)}
        }
    }
}