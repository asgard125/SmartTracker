package com.example.smartTracker.signScreen

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartTracker.R
import com.example.smartTracker.data.Habit
import com.example.smartTracker.data.User
import com.example.smartTracker.mainScreen.habits.HabitsNotificationManager
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.lang.Exception

class SignService : IntentService("SignService"){

    override fun onHandleIntent(intent: Intent?) {
        var user = intent?.getParcelableExtra<User>(C.USER)
        val responseIntent : Intent
        when(intent?.getStringExtra(C.TASK_TYPE)){
            C.SIGN_IN_TASK ->{
                val response = signInUser(user)

                val statusCode = response.code

                if(statusCode == C.OK_CODE){

                    val responseJson = JSONObject(response.body?.string())

                    if(responseJson.get(C.RESULT) == C.RESULT_OK){
                        if(user != null){
                            user.apiKey = responseJson.getJSONObject(C.MESSAGE).getString(C.API_KEY)
                            user = downloadBasicUserInfo(user)
                            saveBasicUserInfo(user)
                        }
                        val habits = downloadAllHabits(user?.userId, user?.apiKey)
                        setUpHabitsNotifications(habits)
                        saveUserDataToDatabase(habits)
                        responseIntent = getResponseIntent(C.SIGN_IN_STATUS, C.STATUS_OK, getString(R.string.login_completed))
                        sendBroadcast(responseIntent)
                    }else{
                        when(responseJson.get(C.MESSAGE)){
                            C.INVALID_LOGIN_PASSWORD_ERROR -> {
                                responseIntent = getResponseIntent(C.SIGN_IN_STATUS, C.STATUS_FAIL, getString(R.string.invalid_login_password))
                                sendBroadcast(responseIntent)
                            }
                        }
                    }
                }else{
                    Log.d("SmartTracker", "SignUp, connection error, code is ${response.code}, message is ${response.message}")
                    responseIntent = getResponseIntent(C.SIGN_UP_STATUS, C.STATUS_FAIL, getString(R.string.connection_error))
                    sendBroadcast(responseIntent)
                }
            }
            C.SIGN_UP_TASK ->{

                val response = signUpUser(user)

                val statusCode = response.code

                if(statusCode == C.OK_CODE){

                    val responseJson = JSONObject(response.body?.string())

                    if(responseJson.get(C.RESULT) == C.RESULT_OK){
                        responseIntent = getResponseIntent(C.SIGN_UP_STATUS, C.STATUS_OK, getString(R.string.registration_completed))
                        responseIntent.putExtra(C.USER, user)
                        sendBroadcast(responseIntent)
                    }else{
                        responseIntent = getResponseIntent(C.SIGN_UP_STATUS, C.STATUS_FAIL, responseJson.getString(C.MESSAGE))
                        sendBroadcast(responseIntent)
                    }
                }else{
                    Log.d("SmartTracker", "SignIn, connection error, code is ${response.code}, message is ${response.message}")
                    responseIntent = getResponseIntent(C.SIGN_UP_STATUS, C.STATUS_FAIL, getString(R.string.connection_error))
                    sendBroadcast(responseIntent)
                }
            }
            C.CHECK_SECRET_KEY_TASK ->{
                val url = C.CHECK_SECRET_KEY_URL+C.SECRET_KEY

                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val statusCode = response.code

                if(statusCode == C.OK_CODE){
                    val responseJson = JSONObject(response.body?.string())
                    responseIntent = Intent(C.ACTION_SIGN_SERVICE+"1")
                    responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
                    responseIntent.putExtra(C.RESULT, responseJson.getString(C.RESULT))
                    sendBroadcast(responseIntent)
                }else{
                    Log.d("SmartTracker", "SignService : checkSecretKey, ${response.message}")
                }
            }
        }
    }

    private fun signInUser(user : User?) : Response{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(C.SIGN_IN_URL+"?${C.LOGIN}=${user?.login}&${C.PASSWORD}=${user?.password}")
            .get()
            .build()

        return client.newCall(request).execute()
    }

    private fun signUpUser(user : User?) : Response {
        val map = mapOf(C.NAME to user?.name, C.LOGIN to user?.login, C.PASSWORD to user?.password)
        val json = JSONObject(map)

        val client = OkHttpClient()
        val type = "application/json".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(type)

        val request = Request.Builder()
            .url(C.SIGN_UP_URL)
            .post(body)
            .build()

        return client.newCall(request).execute()
    }

    private fun getResponseIntent(statusType : String, status : String, message : String) : Intent{
        val responseIntent = Intent()
        responseIntent.action = C.ACTION_SIGN_SERVICE
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
        responseIntent.putExtra(statusType, status)
        responseIntent.putExtra(C.MESSAGE, message)
        return responseIntent
    }

    private fun downloadBasicUserInfo(user : User) : User {
        val client = OkHttpClient()

        val url = C.GET_BASIC_INFO_URL+"?${C.INFO_TYPE}=${C.INFO_TYPE_PRIVATE}&${C.API_KEY}=${user.apiKey}"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()
        val statusCode = response.code

        if(statusCode == C.OK_CODE){

            val responseJson = JSONObject(response.body?.string())

            val jsonUser = responseJson.getJSONObject(C.USER)
            user.userId = jsonUser.getLong(C.ID)
            user.name = jsonUser.getString(C.NAME)
            user.rating = jsonUser.getLong(C.RATING)
            return user
        }else{
            throw Exception("SignService : Connection error")
        }
    }

    private fun saveBasicUserInfo(user : User){
        val sharedPreferences = applicationContext.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(C.ID, user.userId)
        editor.putString(C.API_KEY, user.apiKey)
        editor.putString(C.LOGIN, user.login)
        editor.putString(C.NAME, user.name)
        editor.putLong(C.RATING, user.rating)
        editor.apply()
    }

    private fun downloadAllHabits(userId : Long?, apiKey : String?) : ArrayList<Habit>{

        val client = OkHttpClient()

        val url = C.GET_ALL_HABITS_URL+"?${C.habitType}=all&${C.INFO_TYPE}=detail&${C.API_KEY}=${apiKey}&${C.USER_ID}=$userId"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()
        val statusCode = response.code
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
                habit.votes = habitJson.getInt(C.votes)
                habit.reputation = habitJson.getInt(C.reputation)
                habit.isBooting = habitJson.getBoolean(C.booting)
                habit.isPublic = habitJson.getString(C.type) == "public"
                habit.isMuted = habitJson.getBoolean(C.muted)
                habits.add(habit)
            }
            return habits
        }else{
            Log.d("SmartTracker", "ProfileService, status code is $statusCode")
            return ArrayList<Habit>()
        }
    }

    private fun setUpHabitsNotifications(habits: ArrayList<Habit>){
        val notificationManager = HabitsNotificationManager(applicationContext)
        for(habit in habits) {
            if (!habit.isMuted) {
                notificationManager.setNewAlarms(habit)
            }
        }
    }

    private fun saveUserDataToDatabase(habits : ArrayList<Habit>){
        for(habit in habits){
            Database.HabitsModel.addHabit(habit)
        }
    }


}