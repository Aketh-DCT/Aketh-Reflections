package gr.aketh.echoes.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

object Language {

    @Composable
    fun test(navController: NavController, te: Int)
    {
        Text(text = stringResource(id = te))


    }
}