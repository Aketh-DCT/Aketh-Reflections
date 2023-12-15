package gr.aketh.echoes.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Games : Screen("games", "Games", Icons.Default.Home)
    object Language : Screen("language", "Language", Icons.Default.Menu)
    object ContactUs : Screen("contactus", "Contact Us", Icons.Default.Info)
}