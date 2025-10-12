package com.mwinensoaa.schoolsiren

import android.R
import android.app.Application
import android.app.Notification
import androidx.core.app.NotificationCompat

class AlarmApp : Application() {

    companion object{
        var ID = "com.mwinensoaa.schoolsiren";
    }

    override fun onCreate() {
        super.onCreate()
        buildNotification("Alarm")
    }

    private fun buildNotification(content: String): Notification {
        return NotificationCompat.Builder(this, ID)
            .setContentTitle("Siren")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

    }
}