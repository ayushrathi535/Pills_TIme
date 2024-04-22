package com.example.pillstime.interfaces

import com.example.pillstime.model.Days
import java.sql.Time

interface DaySelectionListener {
    fun onDaysSelected(selectedDays: List<Days>)
}



