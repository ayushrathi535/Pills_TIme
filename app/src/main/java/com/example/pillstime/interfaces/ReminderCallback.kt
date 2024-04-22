package com.example.pillstime.interfaces

import com.example.pillstime.model.ReminderState

interface ReminderCallback {
    fun onReminderStateSelected(reminderState: List<ReminderState>)
}