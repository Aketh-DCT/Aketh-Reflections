package gr.aketh.echoes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import gr.aketh.echoes.databinding.ActivityMain2Binding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        //set the drawer
        drawer = findViewById(R.id.drawer_layout_test)
        var navigationView: NavigationView = findViewById(R.id.nav_viewmain)
        navigationView.setNavigationItemSelectedListener(this)



        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MessageFragment()).commit()

            navigationView.setCheckedItem(R.id.nav_message)
        }



    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed()

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_message -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MessageFragment()).commit()
            }
            R.id.nav_battery -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, BatteryFragment()).commit()
            }
            R.id.nav_send -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SendFragment()).commit()
            }


            else ->
            {

            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true;
    }

}