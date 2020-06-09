package com.example.smartTracker.signScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartTracker.mainScreen.MainActivity
import com.example.smartTracker.R
import com.example.smartTracker.objects.C

class SignActivity : AppCompatActivity(), SignFragmentCallback{

    private var isNeedUpdate = false

    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val result = intent?.getStringExtra(C.RESULT)
            if(result == C.RESULT_OK){
                getApiKey()?.let{
                    signIn()
                }
                supportFragmentManager.beginTransaction().add(R.id.SignContainer, SignInFragment(), C.SIGN_IN_TASK).commit()
            }else{
                supportFragmentManager.beginTransaction().add(R.id.SignContainer, UpdateFragment(), C.SIGN_IN_TASK).commit()
            }
        }

    }

    override fun onBackPressed() {
        when {
            isNeedUpdate -> {
                super.onBackPressed()
            }
            supportFragmentManager.backStackEntryCount != 1 -> {
                supportFragmentManager.popBackStack()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign)

        filter = IntentFilter(C.ACTION_SIGN_SERVICE+"1")
        filter.addCategory(Intent.CATEGORY_DEFAULT)

        startService(Intent(baseContext, SignService::class.java).putExtra(C.TASK_TYPE, C.CHECK_SECRET_KEY_TASK))
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun setSignUpFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.SignContainer, SignUpFragment(), C.SIGN_UP_TASK).addToBackStack(null).commit()
    }

    override fun signIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun setSignInInfo(login: String, password: String) {
        (supportFragmentManager.findFragmentByTag(C.SIGN_IN_TASK) as SignInFragment).setLoginAndPassword(login, password)
    }

    private fun getApiKey() : String?{
        val preferences = this.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences?.getString(C.API_KEY, null)
        return apiKey
    }

}