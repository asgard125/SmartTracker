package com.example.smartTracker.objects

import java.lang.StringBuilder

object C {

    const val SECRET_KEY = "smarttrackerbetav1.0_oasf13dl1s"
    const val ACTION_NEW_DAY_UPDATE_UI = "com.example.smartTracker.DataUpdateReceiver"

    //HTTP
    const val OK_CODE = 200
    const val OUT_SITE = "https://vk.com/greamteamdev"
    const val SIGN_UP_URL = "https://smarttracker.herokuapp.com/register"
    const val SIGN_IN_URL = "https://smarttracker.herokuapp.com/login"
    const val GET_BASIC_INFO_URL = "https://smarttracker.herokuapp.com/api/v1/user/0"
    const val ADD_NEW_HABIT_URL = "https://smarttracker.herokuapp.com/api/v1/habits"
    const val DELETE_HABIT_URL = "https://smarttracker.herokuapp.com/api/v1/habit/"
    const val UPDATE_HABIT_URL = "https://smarttracker.herokuapp.com/api/v1/habit/"
    const val RATING_URL = "https://smarttracker.herokuapp.com/api/v1/users"
    const val COMPLETE_HABIT_URL = "https://smarttracker.herokuapp.com/habit_completed/"
    const val CHECK_SECRET_KEY_URL = "https://smarttracker.herokuapp.com/check_secret_key/"
    const val GET_ALL_HABITS_URL = "https://smarttracker.herokuapp.com/api/v1/habits"

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
    const val ID = "id"
    const val USER_ID = "user_id"
    const val NAME = "name"
    const val LOGIN = "login"
    const val PASSWORD = "password"
    const val RATING = "rating"
    const val API_KEY = "api_key"
    //User Info

    //SignService
    const val ACTION_SIGN_SERVICE = "com.example.smartTracker.signScreen.SignService"
    const val SIGN_IN_TASK = "SIGN_IN"
    const val SIGN_UP_TASK = "SIGN_UP"
    const val CHECK_SECRET_KEY_TASK = "CHECK_SECRET_KEY_TASK"
    const val SIGN_UP_STATUS = "SIGN_UP_STATUS"
    const val SIGN_IN_STATUS = "SIGN_IN_STATUS"
    const val INVALID_LOGIN_PASSWORD_ERROR = "invalid login or password"
    const val LOGIN_EXISTS_ERROR = "user with this login already exists"
    const val INFO_TYPE = "info_type"
    const val INFO_TYPE_PRIVATE = "private"
    const val INFO_TYPE_PUBLIC= "public"
    //SignService

    //HabitsService
        const val ACTION_HABITS_SERVICE = "com.example.smartTracker.mainScreen.habits.HabitsService"
        const val GET_ALL_HABITS_TASK = 1
        const val ADD_DEFAULT_HABIT_TASK = 2
        const val DELETE_HABIT_TASK = 3
        const val UPDATE_HABIT_TASK = 4
        const val COMPLETE_HABIT_TASK = 5
        const val habitId = "habit_id"
    //HabitsService

    //RatingService
        const val ACTION_RATING_SERVICE = "com.example.smartTracker.mainScreen.mainScreen.rating.RatingService"
        const val USERS = "users"
        const val LIMIT = "limit"
        const val OFFSET = "offset"
        const val CURRENT_USER_PLACE = "current_user_place"
        const val CURRENT_USER_RATING = "current_user_rating"
    //RatingService

    //Habits database
    const val habit = "habit"
    const val habits = "habits"
    const val id = "_id"
    const val name = "name"
    const val description = "description"
    const val pluses = "pluses"
    const val minuses = "minuses"
    const val habitType = "habit_type"
    const val booting = "booting"
    const val weekdays = "weekdays"
    const val notifyTime = "notify_time"
    const val votes = "votes"
    const val reputation = "reputation"
    const val muted = "muted"
    const val isDone = "isDone"
    const val serverId = "server_id"
    //Habits database

    //Goals database
    const val goals = "goals"
    //Goals database

    const val ACTION_PROFILE_SERVICE = "com.example.smartTracker.mainScreen.rating.ProfileService"

}