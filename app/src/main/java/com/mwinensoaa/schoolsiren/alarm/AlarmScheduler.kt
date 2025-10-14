package com.mwinensoaa.schoolsiren.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mwinensoaa.schoolsiren.MainActivity
import com.mwinensoaa.schoolsiren.data.AlarmEntity


import java.util.Calendar


object AlarmScheduler {



      ///schedules alarms
    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = getAlarmTimeCalendar(alarm.hour, alarm.minute)
        val triggerTimeMillis = calendar.timeInMillis

         val alarmRequestCode = (alarm.hour * 100) + alarm.minute

        val operationIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.id)
        }

        val operationPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            operationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val showPendingIntent = PendingIntent.getActivity(
            context,
            alarmRequestCode,
            showIntent,
            getPendingIntentFlags(isMutable = false)
        )

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            triggerTimeMillis,
            showPendingIntent
        )
        alarmManager.setAlarmClock(alarmClockInfo, operationPendingIntent)

    }


    //cancel an alarm
    fun cancel(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = (alarm.hour * 100) + alarm.minute
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }


    ///returns the alarm time. if the alarm time has passed. it will shift it to the next day.
    private fun getAlarmTimeCalendar(hour: Int, minute: Int): Calendar {
        val now = Calendar.getInstance()
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarmTime.before(now)) {
            alarmTime.add(Calendar.DAY_OF_YEAR, 1)
        }
        return alarmTime
    }

    private fun getPendingIntentFlags(isMutable: Boolean): Int {
        return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isMutable) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

    }


}

