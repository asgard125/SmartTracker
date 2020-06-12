package com.example.smartTracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Goal(var name : String = "") : Parcelable