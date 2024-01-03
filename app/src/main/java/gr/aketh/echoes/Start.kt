package gr.aketh.echoes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gr.aketh.echoes.classes.FtpClientConnect
import gr.aketh.echoes.composables.ContactUs.FormM
import gr.aketh.echoes.composables.Games
import gr.aketh.echoes.composables.Screen
import gr.aketh.echoes.ui.theme.Main_Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset
import java.util.Locale

class Start : ComponentActivity() {
    var nameAndjsonFiles: List<Pair<String, JSONObject>>? = null

    //Permission stuff
    val permissionsStr = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )

    var permissionsCount = 0
    var permissionsList: ArrayList<String> = ArrayList()

    private lateinit var  sharedPreferences: SharedPreferences
    private lateinit var languageCode: String



    //This was a tough one. To ask multiple permissions
    //You need to first request them, and handle each case
    //When you don't get it etc. This one handles it
    //Do Not touch. Literally, if you touch it dies
    //It is quite complex, i understand 38% of it
    //So don't try please!
    val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        object : ActivityResultCallback<Map<String, Boolean>> {
            override fun onActivityResult(result: Map<String, Boolean>) {
                val list: ArrayList<Any?> = ArrayList(result.values)
                var rejList = arrayListOf<String>()
                permissionsList.clear()
                permissionsCount = 0
                for (i in list.indices) {
                    if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                        permissionsList.add(permissionsStr[i])
                    } else if (!hasPermission(applicationContext, permissionsStr[i])) {
                        //Toast.makeText(applicationContext, permissionsStr[i], Toast.LENGTH_SHORT)
                         //   .show()
                        permissionsCount++
                        Log.d("PERMISSION", permissionsStr[i])
                        rejList.add(permissionsStr[i])
                    }
                }
                if (permissionsList.size > 0) {
                    //Some permissions are denied and can be asked again.
                    askForPermissions(permissionsList)
                } else if (permissionsCount > 0) {
                    //Show alert dialog
                    if(rejList.contains("android.permission.WRITE_EXTERNAL_STORAGE")){
                        //Start Game update
                        initGameCodeAfterPermissions()
                    }
                    else{
                        showPermissionDialog()
                    }


                } else {
                    //All permissions granted. Do your stuff 🤞
                    //Toast.makeText(applicationContext, "All Permissions granted", Toast.LENGTH_SHORT)
                    //    .show()
                    initGameCodeAfterPermissions()
                }
            }


        })

    private fun initGameCodeAfterPermissions()
    {
        lifecycleScope.launch {

            val ftpServer = FtpClientConnect(applicationContext)
            ftpServer.start()


            // Set content after FTP code is finished
            withContext(Dispatchers.Main){
                setContent {
                    // Use the result of the computation
                    // Your content code goes here
                }
            }
        }



    }

    //Create the array to ask for the above function
    private fun askForPermissions(permissionsList: ArrayList<String>) {
        //val newPermissionStr = arrayOfNulls<String>(permissionsList.size)

        val newPermissionStr = permissionsList.toTypedArray()

        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr)
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog()
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var alertDialog: AlertDialog? = null
    private fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setMessage("Some permissions are need to be allowed to use this app without any problems.")
            .setPositiveButton("Settings") { dialog, which -> dialog.dismiss() }
        if (alertDialog == null) {
            alertDialog = builder.create()
            if (!alertDialog!!.isShowing) {
                alertDialog!!.show()
            }
        }
    }
    //End of permission stuff



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = baseContext.getSharedPreferences("language_selected", Context.MODE_PRIVATE)


        lifecycleScope.launch {
            nameAndjsonFiles = loadJsonFilesFromAssets()
            //permissionsList.addAll(permissionsStr.toList())
            //askForPermissions(permissionsList)


            //Set content is default. It's a lambda function

            setContent {
                Main_Theme {
                    AppNavigation(nameAndjsonFiles = nameAndjsonFiles, onButtonClick = { jsonFile ->
                        val intent = Intent(applicationContext, GameInfoActivity::class.java)
                        intent.putExtra("jsonFile", jsonFile)

                        startActivity(intent)
                    })
                }





            }


        }
    }

    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = newBase.getSharedPreferences("language_selected", Context.MODE_PRIVATE)
        languageCode = sharedPreferences.getString("language_key", "en")!! // Default to English if not set
        val locale = Locale(languageCode!!)
        val config = newBase.resources.configuration
        Locale.setDefault(locale)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    //Loads the files from Content
    private suspend fun loadJsonFilesFromAssets(): List<Pair<String, JSONObject>> {
        val jsonFiles = mutableListOf<JSONObject>()
        val nameAndjsonFiles = mutableListOf<Pair<String, JSONObject>>()


        withContext(Dispatchers.IO) {
            val files = assets.list("Content") ?: emptyArray()
            val currentLanguage = Locale.getDefault().language

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

                    val gameInfoTmp = jsonObject.getJSONObject("game_info")

                    //Checks if there is language set
                    try {
                        val language = gameInfoTmp.getString("language")
                        //if it is, good
                        if(currentLanguage == language){
                            nameAndjsonFiles.add(Pair(filename, jsonObject))
                        }

                    }catch (_: JSONException){
                        //if not assume it's english
                        if(currentLanguage=="en"){
                            nameAndjsonFiles.add(Pair(filename, jsonObject))
                        }
                        //nameAndjsonFiles.add(Pair(filename, jsonObject))
                    }



                }
            }
        }
        return nameAndjsonFiles
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
    fun AppNavigation(
        nameAndjsonFiles: List<Pair<String, JSONObject>>?,
        onButtonClick: (String) -> Unit
    ) {
        val navController = rememberNavController()
        var currentRoute by remember {
            mutableStateOf(Screen.Games.route)
        }


        val navBackStackEntry by navController.currentBackStackEntryAsState()
        currentRoute = navBackStackEntry?.destination?.route ?: Screen.Games.route

        val items = listOf(
            Screen.Games,
            Screen.Language,
            Screen.ContactUs
        )

        Scaffold(
            bottomBar = {
                BottomNavigation(backgroundColor = Color.White,
                    elevation = 5.dp) {
                    //val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                    //val currentDestination = navBackStackEntry?.destination?.route

                    items.forEach { screen ->

                        BottomNavigationItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            selectedContentColor = colorResource(id = R.color.raising_black),
                            unselectedContentColor = colorResource(id = R.color.payne_grey),
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
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Games.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Games.route) {
                    if (nameAndjsonFiles != null) {
                        Games.Carousel(
                            nameAndjsonFiles = nameAndjsonFiles!!,
                            onButtonClick = onButtonClick,
                            tooltipText = stringResource(id = R.string.tooltip_text)
                        )
                    }
                }
                composable(Screen.Language.route) {
                    onBackPressed() }
                composable(Screen.ContactUs.route) { FormM(stringResource(id = R.string.contact_us))}
            }
        }


    }
}
