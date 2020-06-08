package com.example.smartTracker.mainScreen.habits

import android.app.IntentService
import android.content.Intent
import com.example.smartTracker.data.Habit

class HabitsService : IntentService("HabitsService"){

    companion object{

        const val ACTION_HABITS_SERVICE = "com.example.smartTracker.mainScreen.habits.HabitsService"
        const val HABITS = "habits"
        const val CODE = "code"
        const val GET_ALL_HABITS = 1

    }

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.extras?.getInt(CODE)){
            GET_ALL_HABITS ->{
                val habits = arrayListOf(Habit(), Habit(), Habit(), Habit())
                val responseIntent = Intent()
                responseIntent.action = ACTION_HABITS_SERVICE
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                responseIntent.putParcelableArrayListExtra(HABITS, habits)
                sendBroadcast(responseIntent)
            }
        }
    }

}