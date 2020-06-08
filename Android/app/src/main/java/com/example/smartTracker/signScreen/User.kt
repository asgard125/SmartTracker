package com.example.smartTracker.signScreen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var userId : Long = 0,
                var name : String = "",
                var login : String = "",
                var password : String = "",
                var rating : Long = 0,
                var apiKey : String = "") : Parcelable