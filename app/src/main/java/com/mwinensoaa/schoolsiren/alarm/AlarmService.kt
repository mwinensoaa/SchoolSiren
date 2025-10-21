package com.mwinensoaa.schoolsiren.alarm




import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mwinensoaa.schoolsiren.data.AlarmDatabase
import com.mwinensoaa.schoolsiren.data.AlarmEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

import com.mwinensoaa.schoolsiren.R



class AlarmService : Service() {

    private var mediaPlayer:  MediaPlayer? = null


    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        val alarmId = intent?.getIntExtra("alarmId", -1) ?: -1
        if (alarmId == -1) return START_NOT_STICKY

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


    ///plays the alarm audio
    private fun playAlarm(alarm: AlarmEntity) {
        try {
            mediaPlayer?.release()

            val uri: Uri = if (alarm.audioUri != null) {
                alarm.audioUri.toUri()
            } else {
                val resId = alarm.type.defaultResId
                "android.resource://${applicationContext.packageName}/$resId".toUri()
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = false

                setOnPreparedListener { mp ->
                    mp.start()
                }

                var playCount = 1
                val loopLimit = if (alarm.loopCount <= 0) 1 else alarm.loopCount
                setOnCompletionListener { mp ->
                    if (playCount >= loopLimit) {
                        mp.release()
                        stopSelf()
                    } else {
                        playCount++
                        mp.seekTo(0)
                        mp.start()
                    }
                }
                prepareAsync()
            }

        } catch (e: SecurityException) {

        } catch (e: Exception) {

        }
    }



    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
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
        @SuppressLint("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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


