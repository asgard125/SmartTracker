package com.example.smartTracker.objects

object C {

    //HTTP
    const val OK_CODE = 200
    const val SIGN_UP_URL = "https://smarttracker.herokuapp.com/register"
    const val SIGN_IN_URL = "https://smarttracker.herokuapp.com/login"
    const val GET_BASIC_INFO_URL = "https://smarttracker.herokuapp.com/api/v1/user/0"

    const val MESSAGE = "message"
    const val RESULT = "result"

    const val RESULT_OK = "OK"
    const val RESULT_FAIL = "FAIL"

    const val STATUS_OK = "STATUS_OK"
    const val STATUS_FAIL = "STATUS_FAIL"
    //HTTP

    const val TASK_TYPE = "TASK_TYPE"
    const val MAIN_PREFERENCES = "MAIN_PREFERENCES"

    //User Info
    const val USER = "user"
    const val USER_ID = "id"
    const val NAME = "name"
    const val LOGIN = "login"
    const val PASSWORD = "password"
    const val RATING = "rating"
    const val API_KEY = "api_key"
    //User Info

    //SignService
    const val ACTION_SIGN_SERVICE = "com.example.smartTracker.signScreen.SignService"
    const val SIGN_IN = "SIGN_IN"
    const val SIGN_UP = "SIGN_UP"
    const val SIGN_UP_STATUS = "SIGN_UP_STATUS"
    const val SIGN_IN_STATUS = "SIGN_IN_STATUS"
    const val INVALID_LOGIN_PASSWORD_ERROR = "invalid login or password"
    const val LOGIN_EXISTS_ERROR = "user with this login already exists"
    const val INFO_TYPE = "info_type"
    const val INFO_TYPE_PRIVATE = "private"
    const val INFO_TYPE_PUBLIC= "public"
    //SignService

}