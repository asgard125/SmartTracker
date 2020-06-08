package com.example.smartTracker.signScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartTracker.mainScreen.MainActivity
import com.example.smartTracker.R
import com.example.smartTracker.objects.C

class SignActivity : AppCompatActivity(), SignFragmentCallback{

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount != 1){
            supportFragmentManager.popBackStack()
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApiKey()?.let{
            signIn()
        }

        setContentView(R.layout.activity_sign)

        supportFragmentManager.beginTransaction().add(R.id.SignContainer, SignInFragment(), C.SIGN_IN).commit()
    }

    override fun setSignUpFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.SignContainer, SignUpFragment(), C.SIGN_UP).addToBackStack(null).commit()
    }

    override fun signIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun setSignInInfo(login: String, password: String) {
        (supportFragmentManager.findFragmentByTag(C.SIGN_IN) as SignInFragment).setLoginAndPassword(login, password)
    }

    private fun getApiKey() : String?{
        val preferences = this.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val apiKey = preferences?.getString(C.API_KEY, null)
        return apiKey
    }

}