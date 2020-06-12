package com.example.smartTracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database

//Update all local data and UI when time is 00:00
class DateUpdateReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("SmartTracker", "New day!")
        if(!Database.isSet){
            Database.setUpDatabase(context!!)
        }
        Database.HabitsModel.setAllIsDoneFalse()

        val responseIntent = Intent()
        responseIntent.action = C.ACTION_NEW_DAY_UPDATE_UI
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
        context?.sendBroadcast(responseIntent)

    }

}