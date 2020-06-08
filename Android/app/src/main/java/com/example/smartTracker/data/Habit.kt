package com.example.smartTracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Habit(var id : Long = -1,
                 var name : String = "New habit",
                 var description : String = "My Description",
                 var pluses : ArrayList<String> = arrayListOf("Plus", "Plus2"),
                 var minuses : ArrayList<String> = arrayListOf("Minus", "Minus2"),
                 var weekDays : ArrayList<Int> = arrayListOf(0,3),
                 var notificationTime : String = "12:00",
                 var reputation : Int = 23,
                 var isDone : Boolean = false,
                 var isPublic : Boolean = false,
                 var isMuted : Boolean = true) : Parcelable