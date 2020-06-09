package com.example.smartTracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Habit(var id : Long = -1,
                 var serverId : Long = -1,
                 var name : String = "",
                 var description : String = "",
                 var pluses : ArrayList<String> = arrayListOf(),
                 var minuses : ArrayList<String> = arrayListOf(),
                 var weekdays : ArrayList<Int> = arrayListOf(0, 2, 4),
                 var notifyTime : String = "12:00",
                 var votes : Int = 0,
                 var reputation : Int = 0,
                 var isBooting : Boolean = true,
                 var isDone : Boolean = false,
                 var isPublic : Boolean = true,
                 var isMuted : Boolean = true) : Parcelable