package com.example.smartTracker.mainScreen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.smartTracker.DateUpdateReceiver
import com.example.smartTracker.mainScreen.habits.HabitsFragment
import com.example.smartTracker.R
import com.example.smartTracker.mainScreen.rating.RatingFragment
import com.example.smartTracker.objects.Database
import com.google.android.material.navigation.NavigationView
import java.lang.Exception
import java.util.*

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

        if(!Database.isDatabaseInitialized()){
            Database.setUpDatabase(applicationContext)
        }

        val updateIntent = Intent(applicationContext, DateUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

        toolbar = findViewById(R.id.MainToolBar)
        drawer = findViewById(R.id.MainDrawer)
        navigationView = findViewById(R.id.MainNavigation)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().add(R.id.MainContainer, MainFragment()).commit()
            toolbar.title = getString(R.string.main_screen)
            navigationView.setCheckedItem(R.id.MainFragmentItem)
        }

        setSupportActionBar(toolbar)

        val drawerButton = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawer.addDrawerListener(drawerButton)
        drawerButton.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            val selectedFragment : Fragment
            val selectedTitle : String
            val selectedIndex : Int
            when(item.itemId){
                R.id.MainFragmentItem ->{
                    selectedFragment = MainFragment()
                    selectedTitle = getString(R.string.main_screen)
                    selectedIndex = 0
                }
                R.id.GoalsFragmentItem ->{
                    selectedFragment = GoalsFragment()
                    selectedTitle = getString(R.string.goals)
                    selectedIndex = 1
                }
                R.id.HabitsFragmentItem ->{
                    selectedFragment = HabitsFragment()
                    selectedTitle = getString(R.string.habits)
                    selectedIndex = 2
                }
                R.id.RatingFragmentItem ->{
                    selectedFragment = RatingFragment()
                    selectedTitle = getString(R.string.rating)
                    selectedIndex = 3
                }
                R.id.SettingsFragmentItem ->{
                    selectedFragment = SettingsFragment()
                    selectedTitle = getString(R.string.settings)
                    selectedIndex = 4
                }
                else ->{
                    throw(Exception(""))
                }
            }
            if(!navigationView.menu.getItem(selectedIndex).isChecked){
                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.MainContainer, selectedFragment).commit()
                supportActionBar?.title = selectedTitle
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Glipko", "OnDestroy")
    }

}