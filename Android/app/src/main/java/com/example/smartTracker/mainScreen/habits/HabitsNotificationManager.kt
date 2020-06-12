package com.example.smartTracker.mainScreen.habits

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C
import java.util.*
import kotlin.collections.ArrayList

class HabitsNotificationManager(private val context : Context?){

    private val alarmManager : AlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun deleteTodayAlarm(habit : Habit){

        val weekdays = weekdaysToCalendarWeekdays(habit.weekdays)
        val nowTime = Calendar.getInstance()
        val todayWeekday = nowTime.get(Calendar.DAY_OF_WEEK)

        if(weekdays.contains(todayWeekday)){

            var alarmIntent = Intent(context, HabitsNotificationsReceiver::class.java)
            alarmIntent.action = "${habit.id}-${todayWeekday}-${habit.notifyTime}"
            var alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            alarmManager.cancel(alarmPendingIntent)


            val hours = (habit.notifyTime[0].toString()+habit.notifyTime[1]).toInt()
            val minutes = (habit.notifyTime[3].toString()+habit.notifyTime[4]).toInt()

            val alarmTime = Calendar.getInstance()
            alarmTime.set(Calendar.DAY_OF_WEEK, todayWeekday)
            alarmTime.set(Calendar.HOUR_OF_DAY, hours)
            alarmTime.set(Calendar.MINUTE, minutes)
            alarmTime.set(Calendar.SECOND, 0)
            alarmTime.set(Calendar.MILLISECOND, 0)
            alarmTime.add(Calendar.DAY_OF_YEAR, 7)


            alarmIntent = Intent(context, HabitsNotificationsReceiver::class.java)
            alarmIntent.action = "${habit.id}-${todayWeekday}-${habit.notifyTime}"
            alarmIntent.putExtra(C.name, habit.name)
            alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            alarmManager.setRepeating(AlarmManager.RTC, alarmTime.timeInMillis, AlarmManager.INTERVAL_DAY * 7, alarmPendingIntent)
        }
    }

    fun deleteAlarms(habit: Habit){
        val weekdays = weekdaysToCalendarWeekdays(habit.weekdays)
        var alarmIntent : Intent
        var alarmPendingIntent : PendingIntent
        for(weekday in weekdays){

            alarmIntent = Intent(context, HabitsNotificationsReceiver::class.java)
            alarmIntent.action = "${habit.id}-${weekday}-${habit.notifyTime}"
            alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            alarmManager.cancel(alarmPendingIntent)
        }
    }

    fun setNewAlarms(habit: Habit){
        val weekdays = weekdaysToCalendarWeekdays(habit.weekdays)
        val hours = (habit.notifyTime[0].toString()+habit.notifyTime[1]).toInt()
        val minutes = (habit.notifyTime[3].toString()+habit.notifyTime[4]).toInt()
        var nowTime : Calendar
        var todayWeekday : Int
        var alarmTime : Calendar
        var alarmIntent : Intent
        var alarmPendingIntent : PendingIntent
        for(weekday in weekdays){

            nowTime = Calendar.getInstance()
            todayWeekday = nowTime.get(Calendar.DAY_OF_WEEK)

            alarmTime = Calendar.getInstance()
            alarmTime.set(Calendar.DAY_OF_WEEK, weekday)
            alarmTime.set(Calendar.HOUR_OF_DAY, hours)
            alarmTime.set(Calendar.MINUTE, minutes)
            alarmTime.set(Calendar.SECOND, 0)
            alarmTime.set(Calendar.MILLISECOND, 0)

            if(alarmTime.before(nowTime) || (habit.isDone && todayWeekday == weekday)){
                alarmTime.add(Calendar.DAY_OF_YEAR, 7)
            }

            alarmIntent = Intent(context, HabitsNotificationsReceiver::class.java)
            alarmIntent.action = "${habit.id}-${weekday}-${habit.notifyTime}"
            alarmIntent.putExtra(C.name, habit.name)
            alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.setRepeating(AlarmManager.RTC, alarmTime.timeInMillis, AlarmManager.INTERVAL_DAY * 7, alarmPendingIntent)
        }
    }

    private fun weekdaysToCalendarWeekdays(weekdays : ArrayList<Int>) : ArrayList<Int>{
        val calendarWeekdays = ArrayList<Int>()
        for(i in 0 until weekdays.size){
            calendarWeekdays.add(when(weekdays[i]){
                0 -> 2
                1 -> 3
                2 -> 4
                3 -> 5
                4 -> 6
                5 -> 7
                6 -> 1
                else -> -1
            })
        }
        return calendarWeekdays
    }

}