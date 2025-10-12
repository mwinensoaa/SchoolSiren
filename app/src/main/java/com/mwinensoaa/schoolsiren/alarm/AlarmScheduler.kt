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


    private const val ALARM_REQUEST_CODE = 100
    private const val TAG = "DailyAlarmApp"




    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = getAlarmTimeCalendar(alarm.hour, alarm.minute)
        val triggerTimeMillis = calendar.timeInMillis

        Log.d(TAG, "Next alarm scheduled for: ${calendar.time.toString()}")

         val alarmRequestCode = (alarm.hour * 100) + alarm.minute
        // 2. Define the 'Operation' PendingIntent (What to run when the alarm fires)
        val operationIntent = Intent(context, AlarmReceiver::class.java).apply {
            // Pass the alarm time back to the receiver for rescheduling purposes
            putExtra("alarmId", alarm.id)
        }


        val operationPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            operationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        // 3. Define the 'Show' PendingIntent (What to open when the user taps the status bar icon)
        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            alarmRequestCode,
            showIntent,
            getPendingIntentFlags(isMutable = false)
        )

        // 4. Create the AlarmClockInfo object, which includes the showIntent
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            triggerTimeMillis,
            showPendingIntent
        )
        // 5. Set the alarm using the highly reliable setAlarmClock method
        alarmManager.setAlarmClock(alarmClockInfo, operationPendingIntent)
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis,operationPendingIntent)
    }



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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

