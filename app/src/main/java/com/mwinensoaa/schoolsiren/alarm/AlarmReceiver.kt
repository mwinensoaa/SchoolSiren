package com.mwinensoaa.schoolsiren.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mwinensoaa.schoolsiren.data.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlarmReceiver : BroadcastReceiver() {
     val TAG = "Broadcaster_received"
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarmId", -1)
        Log.d(TAG, "Broadcater received...${alarmId}")

        if (alarmId == -1) return
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmId", alarmId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AlarmDatabase.getInstance(context).alarmDao()
            dao.getById(alarmId)?.let {
               AlarmScheduler.schedule(context, it)
            }
        }
    }




}
