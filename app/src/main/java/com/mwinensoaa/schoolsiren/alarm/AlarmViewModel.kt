package com.mwinensoaa.schoolsiren.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mwinensoaa.schoolsiren.data.AlarmDatabase
import com.mwinensoaa.schoolsiren.data.AlarmEntity
import com.mwinensoaa.schoolsiren.data.AlarmRepository
import com.mwinensoaa.schoolsiren.alarm.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AlarmDatabase.getInstance(application)
    private val dao = db.alarmDao()
    private val alarmRepository = AlarmRepository(dao)

    val alarms: StateFlow<List<AlarmEntity>> = alarmRepository.getAllFlow()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(alarm: AlarmEntity) = viewModelScope.launch {
        val id = alarmRepository.insert(alarm).toInt()
        // reschedule with correct id - fetch saved entity
        val saved = alarmRepository.getById(id)
       saved?.let { AlarmScheduler.schedule(getApplication(), it) }
    }

    fun update(alarm: AlarmEntity) = viewModelScope.launch {
        alarmRepository.update(alarm)
       if (alarm.enabled) AlarmScheduler.schedule(getApplication(), alarm) else AlarmScheduler.cancel(getApplication(), alarm)
    }

    fun delete(alarm: AlarmEntity) = viewModelScope.launch {
       // AlarmScheduler.cancel(getApplication(), alarm)
        alarmRepository.delete(alarm)
    }









}

