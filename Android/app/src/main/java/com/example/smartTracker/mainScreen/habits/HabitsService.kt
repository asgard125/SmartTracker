package com.example.smartTracker.mainScreen.habits

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class HabitsService : IntentService("HabitsService") {

    override fun onHandleIntent(intent: Intent?) {
        val preferences = applicationContext.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences?.getString(C.API_KEY, null)
        when (intent?.extras?.getInt(C.TASK_TYPE)) {
            C.GET_ALL_HABITS_TASK -> {
                val habits = Database.HabitsModel.getAllHabits()
                val responseIntent = Intent()

                val userId = preferences?.getLong(C.ID, -1)

                val client = OkHttpClient()

                val url = C.GET_ALL_HABITS_URL+"?${C.habitType}=all&${C.INFO_TYPE}=short&${C.API_KEY}=${apiKey}&${C.USER_ID}=$userId"

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
                    val habitsReputation = ArrayList<Habit>()
                    var habit : Habit
                    for(i in 0 until habitsJsonArray.length()){
                        habit = Habit()
                        habitJson = habitsJsonArray.get(i) as JSONObject
                        habit.serverId = habitJson.getLong(C.ID)
                        habit.reputation = habitJson.getInt(C.reputation)
                        habitsReputation.add(habit)
                    }
                    try{
                        habits.sortBy { it.serverId }
                        habitsReputation.sortBy { it.serverId }
                        for(i in 0 until habits.size){
                            habits[i].reputation = habitsReputation[i].reputation
                        }
                    }catch(e : ArrayIndexOutOfBoundsException){
                        Toast.makeText(applicationContext, "Out of Bound", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Log.d("SmartTracker", "ProfileService, status code is $statusCode, message is ${response.message}")
                }

                responseIntent.action = C.ACTION_HABITS_SERVICE
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                responseIntent.putParcelableArrayListExtra(C.habits, habits)
                sendBroadcast(responseIntent)
            }
            C.ADD_DEFAULT_HABIT_TASK -> {
                val habit = intent.getParcelableExtra<Habit>(C.habit)!!

                val pluses = Database.listToString(habit.pluses)
                val minuses = Database.listToString(habit.minuses)
                val habitType = if (habit.isPublic) "public" else "private"
                val weekdaysString = ArrayList<String>()
                for (i in 0 until habit.weekdays.size) {
                    weekdaysString.add(habit.weekdays[i].toString())
                }
                val weekdays = Database.listToString(weekdaysString)
                val map = mapOf(
                    C.API_KEY to apiKey,
                    C.name to habit.name,
                    C.description to habit.description,
                    C.pluses to pluses,
                    C.minuses to minuses,
                    C.type to habitType,
                    C.weekdays to weekdays,
                    C.notifyTime to habit.notifyTime,
                    C.muted to habit.isMuted
                )
                val json = JSONObject(map)

                val client = OkHttpClient()

                val type = "application/json".toMediaTypeOrNull()
                val body = json.toString().toRequestBody(type)

                val request = Request.Builder()
                    .url(C.ADD_NEW_HABIT_URL)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val statusCode = response.code

                Log.d("SmartTracker", "HabitsService : addDefaultHabit, status code is $statusCode")

                if (statusCode == C.OK_CODE) {
                    val responseJson = JSONObject(response.body?.string())

                    if (responseJson.get(C.RESULT) == C.RESULT_OK) {
                        val serverId = responseJson.getJSONObject(C.MESSAGE).getLong(C.habitId)

                        Log.d("SmartTracker", "HabitsService : addDefaultHabit, server id is $serverId")

                        Database.HabitsModel.setServerId(habit.id, serverId)
                    } else {
                        Log.d("SmartTracker", "HabitsService : addDefaultHabit, ${responseJson.getString(C.MESSAGE)}")
                    }


                } else {
                    Log.d("SmartTracker", "HabitsService : addDefaultHabit, Connection error")
                }
            }
            C.DELETE_HABIT_TASK -> {
                val id = intent.getLongExtra(C.id, -1)

                val serverId = Database.HabitsModel.deleteHabitAndGetServerId(id)

                Log.d("SmartTracker", "HabitsService : server id of this habit is $serverId")

                val map = mapOf(C.API_KEY to apiKey)
                val json = JSONObject(map)

                val client = OkHttpClient()

                val url = C.DELETE_HABIT_URL + serverId

                val type = "application/json".toMediaTypeOrNull()
                val body = json.toString().toRequestBody(type)

                val request = Request.Builder()
                    .url(url)
                    .delete(body)
                    .build()

                val response = client.newCall(request).execute()
                val statusCode = response.code

                Log.d("SmartTracker", "HabitsService : deleteHabit, status code is $statusCode")

                if (statusCode != C.OK_CODE) {
                    Log.d(
                        "SmartTracker",
                        "HabitsService : addDefaultHabit, Connection error, message is ${response.message}"
                    )
                }
            }
            C.UPDATE_HABIT_TASK -> {
                val habit = intent.getParcelableExtra<Habit>(C.habit)!!
                val oldHabit = intent.getParcelableExtra<Habit>(C.OLD_HABIT)!!
                val updatedPosition = intent.getIntExtra(C.UPDATED_POSITION, -1)
                val serverId = if (habit.serverId != (-1).toLong()) {
                    habit.serverId
                } else {
                    Database.HabitsModel.getServerId(habit.id)
                }

                Database.HabitsModel.updateHabit(habit)

                Log.d("SmartTracker", "HabitsService : changeHabit, server id of this habit is $serverId")

                val pluses = Database.listToString(habit.pluses)
                val minuses = Database.listToString(habit.minuses)
                val habitType = if (habit.isPublic) "public" else "private"
                val weekdaysString = ArrayList<String>()
                for (i in 0 until habit.weekdays.size) {
                    weekdaysString.add(habit.weekdays[i].toString())
                }
                val weekdays =  Database.listToString(weekdaysString)
                val map = mapOf(
                    C.API_KEY to apiKey,
                    C.name to habit.name,
                    C.description to habit.description,
                    C.pluses to pluses,
                    C.minuses to minuses,
                    C.type to habitType,
                    C.weekdays to weekdays,
                    C.notifyTime to habit.notifyTime,
                    C.muted to habit.isMuted
                )
                val json = JSONObject(map)

                val client = OkHttpClient()

                val url = C.UPDATE_HABIT_URL+serverId

                val type = "application/json".toMediaTypeOrNull()
                val body = json.toString().toRequestBody(type)

                val request = Request.Builder()
                    .url(url)
                    .put(body)
                    .build()

                val response = client.newCall(request).execute()
                val statusCode = response.code

                if (statusCode == C.OK_CODE) {
                    val responseJson = JSONObject(response.body?.string())

                    if (responseJson.get(C.RESULT) == C.RESULT_FAIL) {
                        Database.HabitsModel.updateHabit(oldHabit)
                        val responseIntent = Intent()
                        responseIntent.action = C.ACTION_HABIT_UNDO
                        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                        responseIntent.putExtra(C.OLD_HABIT, oldHabit)
                        responseIntent.putExtra(C.UPDATED_POSITION, updatedPosition)
                        sendBroadcast(responseIntent)
                    }

                } else {
                    Log.d("SmartTracker", "HabitsService : updateHabit, Connection error")
                }
            }
            C.COMPLETE_HABIT_TASK ->{
                val id = intent.getLongExtra(C.id, -1)
                val serverId =  Database.HabitsModel.getServerId(id)
                Database.HabitsModel.completeHabit(id)

                val map = mapOf(C.API_KEY to apiKey)
                val json = JSONObject(map)

                val client = OkHttpClient()

                val url = C.COMPLETE_HABIT_URL+serverId

                val type = "application/json".toMediaTypeOrNull()
                val body = json.toString().toRequestBody(type)

                val request = Request.Builder()
                    .url(url)
                    .put(body)
                    .build()

                val response = client.newCall(request).execute()
                val statusCode = response.code

                if(statusCode != C.OK_CODE){
                    Log.d("SmartTracker", "HabitsService : completeHabit, ${response.message}")
                }
            }
        }
    }

}