package com.example.smartTracker.mainScreen.habits

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class HabitsService : IntentService("HabitsService") {

    override fun onHandleIntent(intent: Intent?) {
        val preferences = applicationContext.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences?.getString(C.API_KEY, null)
        val model = HabitsModel(applicationContext)
        when (intent?.extras?.getInt(C.TASK_TYPE)) {
            C.GET_ALL_HABITS_TASK -> {
                val model = HabitsModel(applicationContext)
                val habits = model.getAllHabits()
                val responseIntent = Intent()
                responseIntent.action = C.ACTION_HABITS_SERVICE
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                responseIntent.putParcelableArrayListExtra(C.habits, habits)
                sendBroadcast(responseIntent)
            }
            C.ADD_DEFAULT_HABIT_TASK -> {
                val habit = intent.getParcelableExtra<Habit>(C.habit)!!

                val pluses = model.listToString(habit.pluses)
                val minuses = model.listToString(habit.minuses)
                val habitType = if (habit.isPublic) "public" else "private"
                val weekdaysString = ArrayList<String>()
                for (i in 0 until habit.weekdays.size) {
                    weekdaysString.add(habit.weekdays[i].toString())
                }
                val weekdays = model.listToString(weekdaysString)
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

                        Log.d(
                            "SmartTracker",
                            "HabitsService : addDefaultHabit, server id is $serverId"
                        )

                        model.setServerId(habit.id, serverId)
                    } else {
                        Log.d("SmartTracker", responseJson.getString(C.MESSAGE))
                    }


                } else {
                    Log.d("SmartTracker", "HabitsService : addDefaultHabit, Connection error")
                }
            }
            C.DELETE_HABIT_TASK -> {
                val id = intent.getLongExtra(C.id, -1)

                val serverId = model.deleteHabitAndGetServerId(id)

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
                val serverId = if (habit.serverId != (-1).toLong()) {
                    habit.serverId
                } else {
                    model.getServerId(habit.id)
                }

                model.updateHabit(habit)

                Log.d("SmartTracker", "HabitsService : server id of this habit is $serverId")

                val pluses = model.listToString(habit.pluses)
                val minuses = model.listToString(habit.minuses)
                val habitType = if (habit.isPublic) "public" else "private"
                val weekdaysString = ArrayList<String>()
                for (i in 0 until habit.weekdays.size) {
                    weekdaysString.add(habit.weekdays[i].toString())
                }
                val weekdays = model.listToString(weekdaysString)
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

                    if (responseJson.get(C.RESULT) != C.RESULT_OK) {
                        Log.d("SmartTracker", responseJson.getString(C.MESSAGE))
                    }

                } else {
                    Log.d("SmartTracker", "HabitsService : updateHabit, Connection error")
                }
            }
            C.COMPLETE_HABIT_TASK ->{
                val id = intent.getLongExtra(C.id, -1)
                val serverId = model.getServerId(id)
                model.completeHabit(id)

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