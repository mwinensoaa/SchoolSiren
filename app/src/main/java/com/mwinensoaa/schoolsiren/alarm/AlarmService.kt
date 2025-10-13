package com.mwinensoaa.schoolsiren.alarm




import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
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

import com.mwinensoaa.schoolsiren.R



class AlarmService : Service() {
    private var player: MediaPlayer? = null
    private var mediaPlayer:  MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        try {
            // Release any previous player
            mediaPlayer?.release()

            val uri: Uri = if (alarm.audioUri != null) {
                // Custom audio selected by the user
                alarm.audioUri!!.toUri()
            } else {
                // Default app sound from res/raw
                val resId = alarm.type.defaultResId
                "android.resource://${applicationContext.packageName}/$resId".toUri()
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                mediaPlayer?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = false

                // Prepare asynchronously to avoid blocking
                setOnPreparedListener { mp ->
                    mp.start()
                }

                // Handle looping
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
            e.printStackTrace()
            Log.e("AlarmService", "Missing storage permission for custom audio: ${e.message}")
            // TODO: gracefully handle missing permission, e.g. play default sound
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("AlarmService", "Error playing alarm: ${e.message}")
        }
    }


//    private fun playAlarm(alarm: AlarmEntity) {
//        val uriStr = alarm.audioUri ?: run {
//            val resId = alarm.type.defaultResId
//            "android.resource://${packageName}/$resId"
//        }
//
//        val uri = uriStr.toUri()
//
//        ///PASTED THIS NEW INSTANCE OF MEDIA PLAYER TO CHECK WHICH ONE WORKS BETTER
//
//        try {
//            mediaPlayer?.setDataSource(applicationContext, uri)
//            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_ALARM)
//            mediaPlayer?.prepare()
//            mediaPlayer?.start()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // fallback to default
//        }
//
//        mediaPlayer?.release()
//        mediaPlayer = MediaPlayer.create(applicationContext, uri).apply {
//            isLooping = false
//            setOnCompletionListener {
//                // decide based on loopCount
//            }
//            start()
//        }
//
//        // Handle loop by restarting on completion up to loopCount
//        var played1 = 1
//        val loops2 = if (alarm.loopCount <= 0) 1 else alarm.loopCount
//        mediaPlayer?.setOnCompletionListener {
//            if (played1 >= loops2) {
//                stopSelf()
//            } else {
//
//                played1++
//                it.start()
//            }
//        }
//
//        ///END OF DIFFERENT INSTANCE OF MEDIA MEDIA. REVERSE TO OLD IF IT DOES NOT WORK TOO
//
//
////        player?.release()
////        player = MediaPlayer.create(applicationContext, uri).apply {
////            isLooping = false
////            setOnCompletionListener {
////                // decide based on loopCount
////            }
////            start()
////        }
////
////        // Handle loop by restarting on completion up to loopCount
////        var played = 1
////        val loops = if (alarm.loopCount <= 0) 1 else alarm.loopCount
////        player?.setOnCompletionListener {
////            if (played >= loops) {
////                stopSelf()
////            } else {
////
////                played++
////                it.start()
////            }
////        }
//    }


    override fun onDestroy() {
        player?.release()
        player = null
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


