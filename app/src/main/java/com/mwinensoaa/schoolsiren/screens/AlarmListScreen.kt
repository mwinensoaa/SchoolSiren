package com.mwinensoaa.schoolsiren.screens

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mwinensoaa.schoolsiren.alarm.AlarmViewModel
import com.mwinensoaa.schoolsiren.data.AlarmEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.isDigit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmListScreen(viewModel: AlarmViewModel = viewModel()) {
    val alarms = viewModel.alarms.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedules") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(alarms) { alarm ->
                AlarmRow(
                    alarm = alarm,
                    onToggle = { updated -> viewModel.update(updated) },
                    onDelete = { viewModel.delete(it) }
                )
            }
        }

        if (showDialog) {
            CreateAlarmDialog(
                viewModel,
                onDismiss = { showDialog = false },
                onSave = { newAlarm ->
                    viewModel.insert(newAlarm)
                    showDialog = false
                }
            )
        }
    }
}



@Composable
fun AlarmRow(
    alarm: AlarmEntity,
    onToggle: (AlarmEntity) -> Unit,
    onDelete: (AlarmEntity) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatTime(getAlarmTimeCalendar(alarm.hour, alarm.minute).timeInMillis),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = { onToggle(alarm.copy(enabled = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                        checkedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                IconButton(onClick = { onDelete(alarm) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmDialog(
    viewModel: AlarmViewModel = viewModel(),
    onDismiss: () -> Unit,
    onSave: (AlarmEntity) -> Unit
) {
    val ctx = LocalContext.current

    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            ctx.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.audioUri = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val finalUri = viewModel.audioUri?.toString()
                        ?: "android.resource://${ctx.packageName}/${viewModel.type.defaultResId}"
                    val alarm = AlarmEntity(
                        hour = viewModel.hour,
                        minute = viewModel.minute,
                        label = viewModel.label.ifBlank { viewModel.type.label },
                        loopCount = viewModel.loops.toInt(),
                        audioUri = finalUri,
                        type = viewModel.type,
                        enabled = true
                    )
                    onSave(alarm)
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("New Alarm") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = viewModel.label,
                    onValueChange = { viewModel.label = it.toTitleCase() },
                    label = { Text("Event description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        TimePickerDialog(
                            ctx,
                            { _, h, m -> viewModel.hour = h; viewModel.minute = m },
                            viewModel.hour,
                            viewModel.minute,
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Select time: %02d:%02d".format(viewModel.hour, viewModel.minute))
                }

                Spacer(Modifier.height(12.dp))

                Text("Alarm type", style = MaterialTheme.typography.labelLarge)
                DropdownMenuType(typeSelection = viewModel.type, onSelect = { viewModel.type = it })

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = viewModel.loops,
                    onValueChange =   {input ->
                        val digits = input.filter { it.isDigit() }
                        val newNumber = if (digits.isBlank()) {
                            0
                        } else {
                            digits.trimStart('0').ifEmpty { "0" }.toInt()
                        }
                        viewModel.loops = newNumber.toString()
                    },
                    modifier = Modifier.width(100.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { audioPicker.launch(arrayOf("audio/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AudioFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (viewModel.audioUri == null) "Pick custom audio" else "Audio selected")
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}


fun String.toTitleCase(): String {

    return this.split(" ").joinToString(" ") { word ->
        if (word.isNotEmpty()) {
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        } else {

            ""
        }
    }
}



fun formatTime(epoch: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(epoch))
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
