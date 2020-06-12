package com.example.smartTracker.mainScreen.rating

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartTracker.R
import com.example.smartTracker.objects.C
import com.example.smartTracker.data.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class RatingService : IntentService("RatingService") {

    private val limit = 50
    private val offset = 0

    override fun onHandleIntent(intent: Intent?) {
        val preferences = applicationContext.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences?.getString(C.API_KEY, null)

        val url = "${C.RATING_URL}?${C.API_KEY}=${apiKey}&${C.LIMIT}=${limit}&${C.OFFSET}=${offset}"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()
        val statusCode = response.code

        if(statusCode == C.OK_CODE){

            val responseIntent = Intent()
            responseIntent.action = C.ACTION_RATING_SERVICE
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
            val users = ArrayList<User>()

            val responseJson = JSONObject(response.body?.string())

            Log.d("SmartTracker", responseJson.toString())

            val currentUserPlace = responseJson.getInt(C.CURRENT_USER_PLACE)
            val currentUserRating = responseJson.getLong(C.CURRENT_USER_RATING)
            val currentUserId = preferences.getLong(C.ID, 0)

            val currentUser = User(
                userId = currentUserId, name = application.getString(R.string.you),
                rating = currentUserRating, ratingPlace = currentUserPlace
            )

            val jsonUsers = responseJson.getJSONArray(C.USERS)
            var jsonUser : JSONObject
            var id : Long
            var name : String
            var rating : Long
            for(i in 0 until jsonUsers.length()){
                jsonUser = jsonUsers.getJSONObject(i)
                id = jsonUser.getLong(C.ID)
                name = jsonUser.getString(C.NAME)
                rating = jsonUser.getLong(C.RATING)
                if(id != currentUserId){
                    users.add(
                        User(
                            userId = id,
                            name = name,
                            rating = rating,
                            ratingPlace = i + 1
                        )
                    )
                }else{
                    users.add(currentUser)
                }
            }
            users.add(currentUser)
            responseIntent.putExtra(C.USERS, users)
            sendBroadcast(responseIntent)
        }else{
            Log.d("SmartTracker", "RatingService : ${response.message}")
        }
    }

}