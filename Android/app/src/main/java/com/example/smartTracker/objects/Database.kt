package com.example.smartTracker.objects

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.smartTracker.R
import com.example.smartTracker.data.Habit
import com.example.smartTracker.data.MainDatabaseHelper
import java.lang.Exception
import java.lang.StringBuilder

object Database {

    private lateinit var context : Context
    private lateinit var helper : MainDatabaseHelper
    lateinit var db : SQLiteDatabase
    var isSet = false

    fun setUpDatabase(context : Context){
        this.context = context
        helper = MainDatabaseHelper(context)
        db = try {
            helper.writableDatabase
        }catch (e : Exception){
            helper.readableDatabase
        }
        isSet = true
    }

    object HabitsModel{

        fun getAllHabits() : ArrayList<Habit>{
            var habit : Habit
            val habits = ArrayList<Habit>()
            val cursor = db.query(C.habits, null, null, null, null, null, null)
            if(cursor.moveToFirst()){
                val idIndex = cursor.getColumnIndex(C.id)
                val serverIdIndex = cursor.getColumnIndex(C.serverId)
                val nameIndex = cursor.getColumnIndex(C.name)
                val descriptionIndex = cursor.getColumnIndex(C.description)
                val plusesIndex = cursor.getColumnIndex(C.pluses)
                val minusesIndex = cursor.getColumnIndex(C.minuses)
                val weekdaysIndex = cursor.getColumnIndex(C.weekdays)
                val notifyTimeIndex = cursor.getColumnIndex(C.notifyTime)
                val votesIndex = cursor.getColumnIndex(C.votes)
                val reputationIndex = cursor.getColumnIndex(C.reputation)
                val bootingIndex = cursor.getColumnIndex(C.booting)
                val typeIndex = cursor.getColumnIndex(C.habitType)
                val mutedIndex = cursor.getColumnIndex(C.muted)
                val isDoneIndex = cursor.getColumnIndex(C.isDone)
                do{
                    habit = Habit()
                    habit.id = cursor.getLong(idIndex)
                    habit.serverId = cursor.getLong(serverIdIndex)
                    habit.name = cursor.getString(nameIndex)
                    habit.description = cursor.getString(descriptionIndex)

                    val plusesRow = cursor.getString(plusesIndex)
                    if(plusesRow.isNotEmpty()){
                        habit.pluses = ArrayList(plusesRow.split(", "))
                    }

                    val minusesRow = cursor.getString(minusesIndex)
                    if(minusesRow.isNotEmpty()){
                        habit.minuses  = ArrayList(minusesRow.split(", "))
                    }

                    val weekdaysRow = cursor.getString(weekdaysIndex)
                    if(weekdaysRow.isNotEmpty()){
                        val weekdaysString = ArrayList(weekdaysRow.split(", "))
                        val weekdaysArray = ArrayList<Int>()
                        for(weekday in weekdaysString){
                            weekdaysArray.add(weekday.toInt())
                        }
                        habit.weekdays = weekdaysArray
                    }

                    habit.notifyTime = cursor.getString(notifyTimeIndex)
                    habit.votes = cursor.getInt(votesIndex)
                    habit.reputation = cursor.getInt(reputationIndex)
                    habit.isBooting = cursor.getInt(bootingIndex) == 1
                    habit.isPublic = cursor.getString(typeIndex) == "public"
                    habit.isMuted = cursor.getInt(mutedIndex) == 1
                    habit.isDone = cursor.getInt(isDoneIndex) == 1
                    habits.add(habit)
                }while(cursor.moveToNext())
            }
            cursor.close()
            return habits
        }

        fun addDefaultHabitAndReturn() : Habit{
            val content = ContentValues()
            val habit = Habit(name = context.getString(R.string.new_habit))
            content.put(C.name, habit.name)
            content.put(C.description, habit.description)
            content.put(C.pluses, "")
            content.put(C.minuses, "")
            content.put(C.weekdays, "0, 2, 4")
            content.put(C.notifyTime, habit.notifyTime)
            content.put(C.votes, 0)
            content.put(C.reputation, 0)
            content.put(C.booting, 1)
            content.put(C.habitType, "public")
            content.put(C.isDone, 0)
            content.put(C.muted, 1)
            val id = db.insert(C.habits, null, content)
            habit.id = id
            return habit
        }

        fun updateHabit(habit : Habit){
            val content = ContentValues()
            content.put(C.name, habit.name)
            content.put(C.description, habit.description)

            val pluses = listToString(habit.pluses)
            content.put(C.pluses, pluses)

            val minuses = listToString(habit.minuses)
            content.put(C.minuses, minuses)

            val weekdaysString = ArrayList<String>()
            for(i in 0 until habit.weekdays.size){
                weekdaysString.add(habit.weekdays[i].toString())
            }
            val weekdays = listToString(weekdaysString)
            content.put(C.weekdays, weekdays)

            content.put(C.notifyTime, habit.notifyTime)
            content.put(C.votes, habit.votes)
            content.put(C.reputation, habit.reputation)
            content.put(C.booting, if(habit.isBooting) 1 else 0)
            content.put(C.habitType, if(habit.isPublic) "public" else "private")
            content.put(C.muted, if(habit.isMuted) 1 else 0)
            content.put(C.isDone, if(habit.isDone) 1 else 0)
            db.update(C.habits, content, "${C.id} = ?", arrayOf(habit.id.toString()))
        }

        fun deleteHabitAndGetServerId(id : Long) : Long{
            val serverId = getServerId(id)
            db.delete(C.habits, "${C.id} = ?", arrayOf(id.toString()))
            Log.d("SmartTracker", "deleting from database $serverId")
            return serverId
        }

        fun completeHabit(id : Long){
            val content = ContentValues()
            content.put(C.isDone, 1)
            db.update(C.habits, content, "${C.id} = ?", arrayOf(id.toString()))
        }

        fun setServerId(id : Long, serverId : Long){
            val content = ContentValues()
            content.put(C.serverId, serverId)
            db.update(C.habits, content, "${C.id} = ?", arrayOf(id.toString()))
            Log.d("SmartTracker", "adding to database $serverId")
        }

        fun getServerId(id : Long) : Long{
            val cursor = db.query(C.habits, arrayOf(C.serverId), "${C.id} = ?", arrayOf(id.toString()), null, null, null)
            cursor.moveToFirst()
            val serverId = cursor.getLong(cursor.getColumnIndex(C.serverId))
            cursor.close()
            return serverId
        }

        fun setAllIsDoneFalse(){
            val cursor = db.query(C.habits, null, null, null, null, null, null)
            if(cursor.moveToFirst()){
                val idIndex = cursor.getColumnIndex(C.id)
                var content : ContentValues
                do {
                    content = ContentValues()
                    content.put(C.isDone, 0)
                    db.update(C.habits, content, "${C.id} = ?", arrayOf(cursor.getLong(idIndex).toString()))
                }while (cursor.moveToNext())
            }
            cursor.close()
        }

    }

    fun listToString(array : ArrayList<String>) : String{
        val string = StringBuilder()
        for(i in 0 until array.size) {
            string.append(array[i])
            if (i != array.size - 1) {
                string.append(", ")
            }
        }
        return string.toString()
    }

}