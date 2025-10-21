package com.mwinensoaa.schoolsiren.data


import com.mwinensoaa.schoolsiren.R

enum class AlarmType(val label: String, val defaultResId: Int) {
    START_LESSONS("Start Lessons", R.raw.audio_lessons_began),
    BREAK("Break Time", R.raw.audio_break_time),
    CHANGE_LESSON("Change Lesson", R.raw.audio_change_lesson),
    CLOSING("Closing Time", R.raw.audio_closing),
    WAKE_UP("Wake Up", R.raw.audio_wake_up),
    PREPS("Preps Time", R.raw.audio_preps_time),
    PREPS_OVER("Preps Over", R.raw.audio_preps_over)
}
