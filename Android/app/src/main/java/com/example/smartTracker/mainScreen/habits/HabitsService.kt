package com.example.smartTracker.mainScreen.habits

import android.app.IntentService
import android.content.Intent
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C

class HabitsService : IntentService("HabitsService"){

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.extras?.getInt(C.TASK_TYPE)){
            C.GET_ALL_HABITS ->{
                val model = HabitsModel(applicationContext)
                val habits = model.getAllHabits()
                val responseIntent = Intent()
                responseIntent.action = C.ACTION_HABITS_SERVICE
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                responseIntent.putParcelableArrayListExtra(C.habits, habits)
                sendBroadcast(responseIntent)
            }
            C.ADD_DEFAULT_HABIT ->{
                val habit = intent.getParcelableExtra<Habit>(C.habit)

            }
        }
    }

}