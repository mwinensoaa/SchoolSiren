package com.mwinensoaa.schoolsiren.alarm




import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mwinensoaa.schoolsiren.data.AlarmDatabase
import com.mwinensoaa.schoolsiren.data.AlarmEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.mwinensoaa.schoolsiren.AlarmApp
import com.mwinensoaa.schoolsiren.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId


class AlarmService : Service() {
    private var player: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ALARM_SERVICE", "The alarm has been triggered...")
        val alarmId = intent?.getIntExtra("alarmId", -1) ?: -1

        if (alarmId == -1) return START_NOT_STICKY
        val notification = createNotification()
        startForeground(1, notification)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AlarmDatabase.getInstance(applicationContext)
            val alarm = db.alarmDao().getById(alarmId)
            alarm?.let {

                withContext(Dispatchers.Main) {
                    playAlarm(it)

                }
            }
        }
        return START_NOT_STICKY
    }

    private fun playAlarm(alarm: AlarmEntity) {
        val uriStr = alarm.audioUri ?: run {
            val resId = alarm.type.defaultResId
            "android.resource://${packageName}/$resId"
        }

        val uri = uriStr.toUri()
        player?.release()
        player = MediaPlayer.create(applicationContext, uri).apply {
            isLooping = false
            setOnCompletionListener {
                // decide based on loopCount
            }
            start()
        }

        // Handle loop by restarting on completion up to loopCount
        var played = 1
        val loops = if (alarm.loopCount <= 0) 1 else alarm.loopCount
        player?.setOnCompletionListener {
            if (played >= loops) {
                stopSelf()
            } else {

                played++
                it.start()
            }
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null




    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val nm = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel("alarm_channel", "Alarms", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }
    }



    private fun createNotification(): Notification {
        val channelId = "alarm_channel"

        // Create channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm is ringing!")
            .setContentText("Tap to open School Siren")
            .setSmallIcon(R.drawable.ic_alarm) // must exist!
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }



}


