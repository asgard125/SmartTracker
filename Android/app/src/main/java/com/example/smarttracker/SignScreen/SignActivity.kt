package com.example.smarttracker.SignScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smarttracker.MainScreen.MainActivity
import com.example.smarttracker.R

class SignActivity : AppCompatActivity(), FragmentCallback{

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount != 1){
            supportFragmentManager.popBackStack()
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        supportFragmentManager.beginTransaction().add(R.id.SignContainer, SignInFragment(), "SignIn").commit()
    }

    override fun setSignUpFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.SignContainer, SignUpFragment(), "SignUp").addToBackStack(null).commit()
    }

    override fun signIn() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun setSignInInfo(email: String, password: String) {
        (supportFragmentManager.findFragmentByTag("SignIn") as SignInFragment).setEmailAndPassword(email, password)
    }
}