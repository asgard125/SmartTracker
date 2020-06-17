package com.example.smartTracker.mainScreen.mainScreen

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

class MainScreenService : IntentService("MainScreenService"){

    companion object{

        const val BEST_HABITS = 0
        const val NEWEST_HABITS = 1
        const val BEST_GOALS = 2
        const val NEWEST_GOALS = 3

    }

    override fun onHandleIntent(intent: Intent?) {
        val preferences = applicationContext.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences.getString(C.API_KEY, "")
        when(intent?.getIntExtra(C.TASK_TYPE, -1)){
            C.GET_LEADERBOARD_TASK ->{
                val client = OkHttpClient()

                val sortType = intent.getIntExtra(C.sortType, -1)
                val sortedBy = if(sortType == BEST_HABITS || sortType == BEST_GOALS){
                    C.POPULARITY
                }else{
                    C.NOVELTY
                }

                val mainUrl = if(sortType == BEST_HABITS || sortType == NEWEST_HABITS){
                    C.GET_ALL_HABITS_URL
                }else{
                    ""
                }


                val url = mainUrl+"?${C.habitType}=public&${C.INFO_TYPE}=detail&${C.API_KEY}=${apiKey}&${C.SORTED_BY}=$sortedBy"

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val statusCode = response.code
                if(mainUrl == C.GET_ALL_HABITS_URL){
                    if(statusCode == C.OK_CODE){
                        val responseJson = JSONObject(response.body?.string())
                        Log.d("SmartTracker", responseJson.toString())
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
                            habit.serverId = habitJson.getLong(C.ID)
                            habit.name = habitJson.getString(C.name)
                            habit.description = habitJson.getString(C.description)
                            habit.reputation = habitJson.getInt(C.reputation)
                            rowPluses = habitJson.getString(C.pluses)
                            if(rowPluses.isNotEmpty()){
                                habit.pluses = ArrayList(rowPluses.split(", "))
                            }
                            rowMinuses = habitJson.getString(C.minuses)
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
                            habit.isVoted = habitJson.getBoolean(C.VOTED)
                            if(habit.isVoted){
                                habit.voteType = habitJson.getString(C.VOTE_TYPE)
                            }
                            habit.userId = habitJson.getLong(C.USER_ID)
                            habits.add(habit)
                        }
                        val responseIntent = Intent()
                        responseIntent.action = C.ACTION_MAIN_LEADERBOARD
                        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                        responseIntent.putParcelableArrayListExtra(C.habits, habits)
                        responseIntent.putExtra(C.sortType, sortType)
                        sendBroadcast(responseIntent)
                    }else{
                        Log.d("SmartTracker", "getLeaderboardAllHabits, status code is $statusCode")
                    }
                }
            }
        }
    }

}