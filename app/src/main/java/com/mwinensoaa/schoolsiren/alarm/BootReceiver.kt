package com.mwinensoaa.schoolsiren.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mwinensoaa.schoolsiren.data.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val db = AlarmDatabase.getInstance(context)
            val dao = db.alarmDao()
            CoroutineScope(Dispatchers.IO).launch {
                val enabled = dao.getAllEnabledAlarms()
                enabled.forEach{AlarmScheduler.schedule(context, it)}
            }
        }
    }
}
