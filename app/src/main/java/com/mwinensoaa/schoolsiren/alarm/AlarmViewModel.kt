package com.mwinensoaa.schoolsiren.alarm

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mwinensoaa.schoolsiren.data.AlarmDatabase
import com.mwinensoaa.schoolsiren.data.AlarmEntity
import com.mwinensoaa.schoolsiren.data.AlarmRepository
import com.mwinensoaa.schoolsiren.data.AlarmType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    var hour by  mutableIntStateOf(0)
    var minute by  mutableIntStateOf(0)
    var label by  mutableStateOf("")

    var loops by  mutableStateOf("0")
    var type by  mutableStateOf(AlarmType.START_LESSONS)
    var audioUri by  mutableStateOf<Uri?>(null)

    var showSuccessDialog by  mutableStateOf(false)
    private val db = AlarmDatabase.getInstance(application)
    private val dao = db.alarmDao()
    private val alarmRepository = AlarmRepository(dao)







    val alarms: StateFlow<List<AlarmEntity>> = alarmRepository.getAllFlow()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(alarm: AlarmEntity) = viewModelScope.launch {
        val id = alarmRepository.insert(alarm).toInt()
        val saved = alarmRepository.getById(id)
       saved?.let { AlarmScheduler.schedule(getApplication(), it) }
    }

    fun update(alarm: AlarmEntity) = viewModelScope.launch {
        alarmRepository.update(alarm)
       if (alarm.enabled) AlarmScheduler.schedule(getApplication(), alarm) else AlarmScheduler.cancel(getApplication(), alarm)
    }

    fun delete(alarm: AlarmEntity) = viewModelScope.launch {
        alarmRepository.delete(alarm)
    }


    fun resetFields(){
        loops = "0"
        label = ""
        audioUri = null
        hour = 0
        minute =0
        type = AlarmType.START_LESSONS

    }









}

