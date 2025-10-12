package com.mwinensoaa.schoolsiren.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val loopCount: Int,
    val audioUri: String?,
    val type: AlarmType,
    val enabled: Boolean = true
)
