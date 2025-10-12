package com.mwinensoaa.schoolsiren.screens

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mwinensoaa.schoolsiren.alarm.AlarmViewModel
import com.mwinensoaa.schoolsiren.data.AlarmEntity
import com.mwinensoaa.schoolsiren.data.AlarmType
import java.util.*
import androidx.compose.ui.Modifier
import com.mwinensoaa.schoolsiren.alarm.AlarmScheduler
import java.time.LocalTime


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAlarmScreen(
    onSaved: () -> Unit,
    vm: AlarmViewModel = viewModel()
) {
    val ctx = LocalContext.current
    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }
    var label by remember { mutableStateOf("") }
    var loops by remember { mutableIntStateOf(0) }
    var type by remember { mutableStateOf(AlarmType.MORNING) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }



    val audioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            ctx.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            audioUri = it
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val cardBg by animateColorAsState(
        targetValue = colorScheme.surfaceVariant,
        label = "CardBackgroundAnimation"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Schedule Alarm") },
                navigationIcon = {
                    IconButton(onClick = onSaved) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val finalUri = audioUri?.toString()
                        ?: "android.resource://${ctx.packageName}/${type.defaultResId}"

                    val alarm = AlarmEntity(
                        hour = hour,
                        minute = minute,
                        label = label.toTitleCase().ifBlank { type.label },
                        loopCount = loops,
                        audioUri = finalUri,
                        type = type,
                        enabled = true
                    )
                    vm.insert(alarm)
                    showSuccessDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Alarm", style = MaterialTheme.typography.titleMedium)
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Label field
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Event description") },
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Time picker card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardBg),
                onClick = {
                    TimePickerDialog(
                        ctx,
                        { _, h, m -> hour = h; minute = m },
                        hour, minute, true
                    ).show()
                }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Alarm time", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "%02d:%02d".format(hour, minute),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        )
                    }
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                }
            }

            // Alarm type
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardBg)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Alarm type", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    DropdownMenuType(
                        typeSelection = type,
                        onSelect = { type = it }
                    )
                }
            }

            // Loop count
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardBg)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Loop count", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = loops.toString(),
                        onValueChange = { loops = it.toIntOrNull() ?: 1 },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // Audio picker
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardBg),
                onClick = { audioPicker.launch(arrayOf("audio/*")) }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Alarm sound", style = MaterialTheme.typography.labelMedium)
                        Text(
                            if (audioUri == null) "Default (${type.label})" else "Custom audio selected",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(Icons.Default.MusicNote, contentDescription = null)
                }
            }
        }
        if (showSuccessDialog) {
            SuccessDialog(
                message = "Alarm saved successfully!",
                onDismiss = {
                    showSuccessDialog = false
                    onSaved() // navigate back or refresh
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuType(
    typeSelection: AlarmType,
    onSelect: (AlarmType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // For accessibility and consistent Material behavior
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = typeSelection.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Alarm type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor() // required for correct dropdown positioning
                .fillMaxWidth()
        )

        // Dropdown content
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AlarmType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = when (type) {
                                    AlarmType.MORNING -> Icons.Default.WbSunny
                                    AlarmType.BREAK -> Icons.Default.Coffee
                                    AlarmType.CHANGE_LESSON -> Icons.Default.Notifications
                                    AlarmType.CLOSING -> Icons.Default.Nightlight
                                    AlarmType.WAKE_UP -> Icons.Default.Alarm
                                    else -> Icons.Default.AccessTime
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(type.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    },
                    onClick = {
                        onSelect(type)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Success!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}



@Composable
fun SaveAlarmDemo(flag: Boolean) {
    var showDialog by remember { mutableStateOf(flag) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDialog = true }) {
            Text("Save Alarm")
        }

        if (showDialog) {
            SuccessDialog(
                message = "Your alarm was saved successfully!",
                onDismiss = { showDialog = false }
            )
        }
    }
}

