package com.example.smartTracker.mainScreen.rating

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartTracker.data.Goal
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ProfileService : IntentService("ProfileService"){

    override fun onHandleIntent(intent: Intent?) {
        val userId = intent?.getLongExtra(C.ID, -1)
        val preferences = applicationContext.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences?.getString(C.API_KEY, null)

        val client = OkHttpClient()

        val url = C.GET_ALL_HABITS_URL+"?${C.habitType}=public&${C.INFO_TYPE}=detail&${C.API_KEY}=${apiKey}&${C.USER_ID}=$userId"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()
        val statusCode = response.code
        if(statusCode == C.OK_CODE){
            val responseJson = JSONObject(response.body?.string())
            val habitsJsonArray = responseJson.getJSONArray(C.habits)
            var habitJson : JSONObject
            val habits = ArrayList<Habit>()
            var habit : Habit
            var rowPluses : String
            var rowMinuses : String
            var rowWeekdays : String
            var weekdaysArray : ArrayList<Int>
            var weekdaysString : ArrayList<String>
            for(i in 0 until habitsJsonArray.length()){
                habit = Habit()
                habitJson = habitsJsonArray.get(i) as JSONObject
                habit.name = habitJson.getString(C.name)
                habit.description = habitJson.getString(C.description)
                habit.reputation = habitJson.getInt(C.reputation)
                rowPluses = habitJson.getString(C.pluses)
                if(rowPluses.isNotEmpty()){
                    habit.pluses = ArrayList(rowPluses.split(", "))
                }
                rowMinuses = habitJson.getString(C.pluses)
                if(rowMinuses.isNotEmpty()){
                    habit.minuses = ArrayList(rowMinuses.split(", "))
                }
                rowWeekdays = habitJson.getString(C.weekdays)
                if(rowWeekdays.isNotEmpty()){
                    weekdaysString = ArrayList(rowWeekdays.split(", "))
                    weekdaysArray = ArrayList()
                    for(weekday in weekdaysString){
                        weekdaysArray.add(weekday.toInt())
                    }
                    habit.weekdays = weekdaysArray
                }
                habit.notifyTime = habitJson.getString(C.notifyTime)
                habits.add(habit)
            }
            val responseIntent = Intent()
            responseIntent.action = C.ACTION_PROFILE_SERVICE
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
            responseIntent.putParcelableArrayListExtra(C.habits, habits)
            responseIntent.putParcelableArrayListExtra(C.goals, ArrayList<Goal>())
            sendBroadcast(responseIntent)
        }else{
            Log.d("SmartTracker", "ProfileService, status code is $statusCode")
        }
    }

}