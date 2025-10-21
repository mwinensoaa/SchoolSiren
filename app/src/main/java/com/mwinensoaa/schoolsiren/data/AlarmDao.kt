package com.mwinensoaa.schoolsiren.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.mwinensoaa.schoolsiren.data.AlarmEntity


@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity): Long

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms ORDER BY hour, minute DESC")
    fun getAllScheduledAlarmsFlow(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms")
     fun getAllAlarmsWhichPlayOnce(): Flow<List<AlarmEntity>>

     @Query("SELECT * FROM alarms WHERE hour=:hour AND minute=:minutes ORDER BY minute DESC")
     fun getAlarmByHourAndMinutes(hour: Int, minutes: Int): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE enabled = 1")
     suspend fun getAllEnabledAlarms(): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE enabled = 0")
     fun getAllDisabledAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): AlarmEntity?


}
