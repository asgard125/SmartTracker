package com.example.smarttracker.SignScreen

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.smarttracker.Objects.Constants
import com.example.smarttracker.Objects.SHA256
import com.example.smarttracker.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SignInAsyncTask(private val context : Context?,
                      private val callback: FragmentCallback) : AsyncTask<String, String, Boolean>(){

    private lateinit var dialog : AlertDialog
    private lateinit var loadingText : TextView

    override fun onPreExecute() {
        super.onPreExecute()
        dialog = AlertDialog.Builder(context).setView(R.layout.dialog_loading).setCancelable(false).create()
        dialog.show()
        loadingText = dialog.findViewById(R.id.LoadingTitle)
    }

    override fun doInBackground(vararg params: String): Boolean {


        publishProgress(context?.getText(R.string.loading).toString())

        val email = params[0]
        val password = params[1]

        val hash = SHA256.hash(password)
        val map = mapOf("email" to email, "password" to hash)
        val json = JSONObject(map)
        Log.d("Glipko", "Json is $json")

        /*val client = OkHttpClient()
        val type = "application/json".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(type)

        val request = Request.Builder()
            .url(Constants.SERVER_URL)
            .post(body)
            .build()
        val response = client.newCall(request).execute()

        val resultCode = response.code

        val apiKey = response.body.toString()

        Log.d("Glipko", "Api key is $apiKey")

        if(resultCode == Constants.OK_CODE){
            return true
        }
        if(resultCode == Constants.NOT_ALLOWED_CODE){
            return false
        }
        throw Exception("There is no such code")*/

        return true
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
        loadingText.text = values[0]
    }

    override fun onPostExecute(canSignIn: Boolean) {
        super.onPostExecute(canSignIn)
        dialog.dismiss()
        if(canSignIn){
            callback.signIn()
        }else{
            Toast.makeText(context, context?.getString(R.string.sign_in_error), Toast.LENGTH_LONG).show()
        }

    }

}