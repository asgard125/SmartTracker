package com.example.smartTracker.mainScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.smartTracker.R
import com.example.smartTracker.mainScreen.habits.HabitsNotificationManager
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database
import com.example.smartTracker.signScreen.SignActivity

class SettingsFragment : Fragment() {

    private lateinit var root : View
    private lateinit var exitButton : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        exitButton = root.findViewById(R.id.SettingExitButton)

        exitButton.setOnClickListener {
            disableHabitsNotifications()
            deleteLocalInfo()
            val intent = Intent(context, SignActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return root
    }

    private fun deleteLocalInfo(){
        Database.db.delete(C.habits, null, null)
        val preferences = context?.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.remove(C.API_KEY)
        editor?.remove(C.NAME)
        editor?.remove(C.RATING)
        editor?.remove(C.ID)
        editor?.remove(C.LOGIN)
        editor?.apply()
    }

    private fun disableHabitsNotifications(){
        val notificationManager = HabitsNotificationManager(context)
        val habits = Database.HabitsModel.getAllHabits()
        for(habit in habits){
            notificationManager.deleteAlarms(habit)
        }
    }

}