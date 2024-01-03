package gr.aketh.echoes

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.os.LocaleListCompat
import gr.aketh.echoes.classes.LocaleHelper
import gr.aketh.echoes.ui.theme.Echoes_AkethTheme
import gr.aketh.echoes.ui.theme.Main_Theme
import java.util.Locale

class Intro : ComponentActivity() {

    private var lang: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedLanguage = getLanguage(this)
        //setLocale(savedLanguage)



        if (savedInstanceState != null) {
            // Restore value of lang from saved state
            lang = savedInstanceState.getString("language_key")
        } else {
            // Probably orientation change occurred, get the value from Intent
            lang = intent.getStringExtra("language_key")
        }

        setContent {
            //window.statusBarColor = getColor(R.color.colombia_blue)
            Main_Theme {

                // A surface container using the 'background' color from the theme
               /* Surface(
                    modifier = Modifier.fillMaxSize()
                    //color = MaterialTheme.colorScheme.background
                ) {

                }

                */
                Greeting2("Android")
            }
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("language_key", lang)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lang = savedInstanceState.getString("language_key")

        // Use lang in your activity
    }

    fun saveLanguage(context: Context, lang: String) {
        val prefs = context.getSharedPreferences("language_selected", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("language_key", lang)
        editor.apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("language_selected", Context.MODE_PRIVATE)
        return prefs.getString("language_key", "en") ?: "en"
    }

    fun setLocale(lang:String)
    {
        val sharedPreferences = getSharedPreferences("language_selected", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language_key",lang)
        editor.apply()
    }



    @Composable
    fun Greeting2(name: String, modifier: Modifier = Modifier) {
        var language_selected by remember { mutableStateOf("en") }

        Box(modifier=modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        {
            Column(modifier = modifier
                .fillMaxSize()
                .padding(15.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                Image(
                    painterResource(id = R.drawable.playing_logo),
                    contentDescription = "Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )


                Text(text = stringResource(id = R.string.intro_text),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(top = 30.dp))

                Text(text = stringResource(id = R.string.select_language),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 30.dp)
                )

                DropDownMenu(onLanguageSelected = { lang_sel ->
                    language_selected = lang_sel
                })
            }
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f)){
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Button(onClick = {
                        changeActivity(language_selected)

                    }, colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.puce)),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 5.0.dp)) {
                        Text(text = "Start", style = MaterialTheme.typography.displaySmall)
                    }


                    Image(painterResource(id = R.drawable.logosbeneficaireserasmusleft_en),
                        contentDescription = "Test",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth(0.8f))
                }
            }
                }




    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Echoes_AkethTheme {
            Greeting2("Android")
        }
    }



    //Changes activity
    fun changeActivity(languageCode: String){
        val intent = Intent(this, Start::class.java)
        intent.putExtra("language",languageCode)
        startActivity(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropDownMenu(onLanguageSelected: (String) -> Unit)
    {
        var context = LocalContext.current

        var isExpanded by remember{
            mutableStateOf(false)
        }

        var language by remember {
            mutableStateOf("")
        }

        var language_selected by remember {
            mutableStateOf("en")
        }



        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange =
        {newValue -> isExpanded = newValue})
        {
            TextField(
                value = language,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                placeholder = {
                    Text(text = "English")
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = {
                isExpanded = false  }) {

                DropdownMenuItem(
                    text = {
                        Text(text = "Greek")
                    },
                    onClick = {
                        language = "Greek"
                        language_selected = "el"
                        isExpanded = false
                        saveLanguage(context, language_selected)
                        setLocale(language_selected)
                        onLanguageSelected(language_selected)
                        //recreate()

                        Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show()

                    })

                DropdownMenuItem(
                    text = {
                        Text(text = "English")
                    },
                    onClick = { language = "English"
                        language_selected = "en"
                        isExpanded = false
                        saveLanguage(context, language_selected)
                        setLocale(language_selected)
                        onLanguageSelected(language_selected)
                        //recreate()

                    }

                )


                DropdownMenuItem(
                    text = {
                        Text(text = "Italian")
                    },
                    onClick = { language = "Italian"
                        language_selected = "it"
                        isExpanded = false
                        saveLanguage(context, language_selected)
                        setLocale(language_selected)
                        onLanguageSelected(language_selected)

                    })






            }
        }
    }


}




