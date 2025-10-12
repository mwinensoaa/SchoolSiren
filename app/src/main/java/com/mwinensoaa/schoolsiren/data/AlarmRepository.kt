package com.mwinensoaa.schoolsiren.data

import kotlinx.coroutines.flow.Flow


class AlarmRepository(private val dao: AlarmDao) {

    fun getAllFlow(): Flow<List<AlarmEntity>> = dao.getAllScheduledAlarmsFlow()
     fun getAllAlarmWhichPlayOnce(): Flow<List<AlarmEntity>> = dao.getAllAlarmsWhichPlayOnce()

    suspend fun getAllEnabledAlarms(): List<AlarmEntity> = dao.getAllEnabledAlarms()
    fun getAllDisabledAlarms(): Flow<List<AlarmEntity>> = dao.getAllDisabledAlarms()
    suspend fun insert(alarm: AlarmEntity): Long = dao.insert(alarm)
    suspend fun update(alarm: AlarmEntity) = dao.update(alarm)
    suspend fun delete(alarm: AlarmEntity) = dao.delete(alarm)
    suspend fun getById(id: Int) = dao.getById(id)

    fun getAlarmByHourAndMinutes(hour: Int, minutes: Int):List<AlarmEntity> = dao.getAlarmByHourAndMinutes(hour, minutes)
}
