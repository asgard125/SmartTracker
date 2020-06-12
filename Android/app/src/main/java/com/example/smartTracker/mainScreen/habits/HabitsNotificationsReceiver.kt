package com.example.smartTracker.mainScreen.habits

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smartTracker.R
import com.example.smartTracker.mainScreen.MainActivity
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database


class HabitsNotificationsReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val habitName = intent?.getStringExtra(C.name)

        Log.d("SmartTracker", "Notification delivered : ${intent?.action} - $habitName")

        val notificationIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_priority)
            .setContentTitle(habitName)
            .setContentText(context?.getString(R.string.habit_notification_text))
            .setContentIntent(contentIntent)

        val notificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName: CharSequence = "smart_tracker"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("123", channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId(notificationChannel.id)

        }

        val notification = builder.build()
        notificationManager.cancel(habitName, 0)
        notificationManager.notify(habitName, 0, notification)
    }

}