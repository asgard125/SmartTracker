package com.example.smarttracker.SignScreen

import androidx.fragment.app.Fragment

interface FragmentCallback {

    fun setSignInInfo(email : String, password : String)
    fun setSignUpFragment()
    fun signIn()

}