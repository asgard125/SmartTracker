package com.example.smartTracker.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.smartTracker.objects.C

class MainDatabaseHelper(context : Context?) : SQLiteOpenHelper(context, "main_database", null , 1){

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE ${C.habits}(${C.id} INTEGER PRIMARY KEY AUTOINCREMENT," + //890
                "${C.serverId} INTEGER," + // 123
                "${C.name} TEXT," + // Habit
                "${C.description} TEXT," + // My habit
                "${C.pluses} TEXT," + // 1 plus,2 plus,3 plus
                "${C.minuses} TEXT," + // 1 minus,2 minus,3 minus
                "${C.weekdays} TEXT," + // 1, 2, 4
                "${C.notifyTime} TEXT," + // 12:00
                "${C.votes} INT," + // 123
                "${C.reputation} INT," + // 34
                "${C.booting} INTEGER," + // 1
                "${C.habitType} TEXT," + // public
                "${C.muted} INTEGER," + // 1
                "${C.isDone} INTEGER)") //
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}