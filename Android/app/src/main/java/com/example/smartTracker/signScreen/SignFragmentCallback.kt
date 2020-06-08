package com.example.smartTracker.signScreen

interface SignFragmentCallback {

    fun setSignInInfo(login : String, password : String)
    fun setSignUpFragment()
    fun signIn()

}