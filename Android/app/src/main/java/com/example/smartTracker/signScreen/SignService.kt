package com.example.smartTracker.signScreen

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartTracker.R
import com.example.smartTracker.data.User
import com.example.smartTracker.objects.C
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
                        responseIntent = getResponseIntent(C.SIGN_IN_STATUS, C.STATUS_OK, getString(R.string.login_completed))
                        sendBroadcast(responseIntent)
                        if(user != null){
                            user.apiKey = responseJson.getJSONObject(C.MESSAGE).getString(C.API_KEY)
                            user = downloadBasicUserInfo(user)
                            saveBasicUserInfo(user)
                        }

                    }else{
                        when(responseJson.get(C.MESSAGE)){
                            C.INVALID_LOGIN_PASSWORD_ERROR -> {
                                responseIntent = getResponseIntent(C.SIGN_IN_STATUS, C.STATUS_FAIL, getString(R.string.invalid_login_password))
                                sendBroadcast(responseIntent)
                            }
                        }
                    }
                }else{
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
                        when(responseJson.get(C.MESSAGE)){
                            C.LOGIN_EXISTS_ERROR-> {
                                responseIntent = getResponseIntent(C.SIGN_UP_STATUS, C.STATUS_FAIL, getString(R.string.login_exists_error))
                                sendBroadcast(responseIntent)
                            }
                        }
                    }

                }else{
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
            user.userId = jsonUser.getLong(C.USER_ID)
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
        editor.putLong(C.USER_ID, user.userId)
        editor.putString(C.API_KEY, user.apiKey)
        editor.putString(C.LOGIN, user.login)
        editor.putString(C.NAME, user.name)
        editor.putLong(C.RATING, user.rating)
        editor.apply()
    }

}