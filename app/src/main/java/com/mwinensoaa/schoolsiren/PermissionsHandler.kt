package com.mwinensoaa.schoolsiren

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
@SuppressLint("ObsoleteSdkInt")
fun RequestPermissions() {
    val context = LocalContext.current

    // Build only the permissions truly needed on this device
    val permissionsToRequest = remember {
        buildList {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // Android 13+ (API 33+)
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    }

                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        add(Manifest.permission.READ_MEDIA_AUDIO)
                    }
                }

                else -> {
                    // Android 12 and below — include READ_EXTERNAL_STORAGE always if not granted
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    // WRITE_EXTERNAL_STORAGE may still be needed for Android 9 and below
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }

    //Launcher for dynamic permission requests
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        result.forEach { (permission, granted) ->
            if (!granted) {
                Toast.makeText(context, "Permission denied: $permission", Toast.LENGTH_SHORT).show()
            }
        }
        // After normal permissions, check for exact alarm
        requestExactAlarmPermissionIfNeeded(context)
    }

    // Battery optimization request
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = context.packageName
            val isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName)

            if (!isIgnoringOptimizations) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //Request only missing permissions once
    LaunchedEffect(Unit) {
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // All granted already — proceed to exact alarm check
            requestExactAlarmPermissionIfNeeded(context)
        }
    }
}

/**
 * Request exact alarm permission if needed (Android 12+)
 */
fun requestExactAlarmPermissionIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Cannot open alarm permission settings", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
