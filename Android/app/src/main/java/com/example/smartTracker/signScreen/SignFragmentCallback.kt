package com.example.smartTracker.signScreen

interface SignFragmentCallback {

    //If registration is successful, automatically fill in sign in forms
    fun setSignInInfo(login : String, password : String)
    //Navigate to sign up fragment
    fun setSignUpFragment()
    //Go to MainActivity
    fun signIn()

}