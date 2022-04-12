package gr.aketh.echoes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import gr.aketh.echoes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var startButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        //set the drawer
        drawer = findViewById(R.id.drawer_layout_test)
        var navigationView: NavigationView = findViewById(R.id.nav_viewmain)
        navigationView.setNavigationItemSelectedListener(this)


        //change which is hit
        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MessageFragment()).commit()

            navigationView.setCheckedItem(R.id.nav_message)
        }







    }

    private fun switchActivities() {
        var switchActivity: Intent = Intent(this@MainActivity,GameTemplate::class.java)
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

            R.id.nav_share ->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GameDescFragment()).commit()
                //Button stuff to change scene

            }


            else ->
            {

            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true;
    }

}