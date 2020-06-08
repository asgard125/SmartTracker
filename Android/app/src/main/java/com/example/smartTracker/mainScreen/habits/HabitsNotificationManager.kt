package com.example.smartTracker.mainScreen.habits

import android.app.AlarmManager
import android.content.Context
import com.example.smartTracker.data.Habit

class HabitsNotificationManager(private val context : Context){

    private val alarmManager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun updateHabitAlarms(oldHabit : Habit, newHabit : Habit){
        deleteOldAlarms(oldHabit)
        setNewAlarms(newHabit)
    }

    fun muteHabit(habit : Habit){

    }

    fun unmuteHabit(habit : Habit){

    }

    private fun deleteOldAlarms(oldHabit: Habit){

    }

    private fun setNewAlarms(newHabit: Habit){

    }

}