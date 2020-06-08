package com.example.smartTracker.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MainDatabase(context : Context?) : SQLiteOpenHelper(context, "main_database", null , 1){

    val habits = "habits"
    val name = "name"
    val description = "description"
    val pluses = "pluses"
    val minuses = "minuses"
    val isDone = "isDone"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $habits($name TEXT," + // Habit
                "$description TEXT" + // My habit
                "$pluses TEXT," + // 1 plus,2 plus,3 plus
                "$minuses TEXT)" + // 1 minus,2 minus,3 minus
                "$isDone")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}