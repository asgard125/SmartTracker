package com.example.smarttracker.Data

import java.util.*

data class Habit(var name : String,
                 var description : String,
                 var isPublic : Boolean,
                 var daysOfWeek : ArrayList<Boolean>,
                 var timeOfNotification : ArrayList<Long>)