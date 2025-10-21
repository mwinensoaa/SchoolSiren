package com.mwinensoaa.schoolsiren

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.ComponentName
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.collections.component1
import kotlin.collections.component2
import androidx.core.net.toUri

@Composable
@SuppressLint("ObsoleteSdkInt")
fun RequestPermissions() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

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

    //Request only missing permissions once
    LaunchedEffect(Unit) {
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // All granted already — proceed to exact alarm check
            requestExactAlarmPermissionIfNeeded(context)
        }
    }

    if (!isIgnoringBatteryOptimizations(context)) {
        ShowBatteryOptimizationDialog(showDialog, onDismiss={showDialog=false})
    }


}

//checks whether the phone's battery is optimized
@SuppressLint("ObsoleteSdkInt")
fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    } else true
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

@OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowBatteryOptimizationDialog(
        showDialog: Boolean,
        onDismiss: () -> Unit
    ) {
        val context = LocalContext.current

        if (showDialog) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(
                        text = "Allow Background Alarms",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        text = "To make sure your alarms ring even when the app is closed, please " +
                                "enable background running and autostart permissions for School Siren."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        openBackgroundSettings(context)
                        onDismiss()
                    }) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
    }




fun openBackgroundSettings(context: Context) {
    try {
        val manufacturer = Build.MANUFACTURER.lowercase()

        val intent = when {
            manufacturer.contains("xiaomi") -> Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            manufacturer.contains("oppo") -> Intent().apply {
                component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            }
            manufacturer.contains("vivo") -> Intent().apply {
                component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
            }
            manufacturer.contains("huawei") -> Intent().apply {
                component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            }
            manufacturer.contains("samsung") -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            manufacturer.contains("infinix") || manufacturer.contains("tecno") || manufacturer.contains("itel") -> Intent().apply {
                component = ComponentName(
                    "com.transsion.phonemaster",
                    "com.transsion.phonemaster.autostart.AutoStartActivity"
                )
            }
            else -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback: open general app settings
            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(fallbackIntent)
        }

    } catch (e: Exception) {
        e.printStackTrace()
        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(fallbackIntent)
    }
}
