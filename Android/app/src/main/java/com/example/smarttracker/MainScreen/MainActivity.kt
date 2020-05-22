package com.example.smarttracker.MainScreen

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.smarttracker.R
import com.google.android.material.navigation.NavigationView
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawer : DrawerLayout
    private lateinit var navigationView : NavigationView

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.MainToolBar)
        drawer = findViewById(R.id.MainDrawer)
        navigationView = findViewById(R.id.MainNavigation)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().add(R.id.MainContainer, MainFragment()).commit()
            toolbar.title = getString(R.string.main_screen)
            navigationView.setCheckedItem(R.id.MainFragmentItem)
        }

        setSupportActionBar(toolbar)

        val drawerButton = ActionBarDrawerToggle(this, drawer, toolbar, R.string.oper_drawer, R.string.close_drawer)
        drawer.addDrawerListener(drawerButton)
        drawerButton.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            val selectedFragment : Fragment
            val selectedTitle : String
            when(item.itemId){
                R.id.MainFragmentItem ->{
                    selectedFragment = MainFragment()
                    selectedTitle = getString(R.string.main_screen)
                }
                R.id.GoalsFragmentItem ->{
                    selectedFragment = GoalsFragment()
                    selectedTitle = getString(R.string.goals)
                }
                R.id.HabitsFragmentItem ->{
                    selectedFragment = HabitsFragment()
                    selectedTitle = getString(R.string.habits)
                }
                R.id.RatingFragmentItem ->{
                    selectedFragment = RatingFragment()
                    selectedTitle = getString(R.string.rating)
                }
                R.id.SettingsFragmentItem ->{
                    selectedFragment = SettingsFragment()
                    selectedTitle = getString(R.string.settings)
                }
                else ->{
                    throw(Exception(""))
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.MainContainer, selectedFragment).commit()
            supportActionBar?.title = selectedTitle
            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

}