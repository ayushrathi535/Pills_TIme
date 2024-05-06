package com.example.pillstime.utils

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {

    @TypeConverter
    fun fromDateList(dateList: List<Date>): String {
        return dateList.joinToString(",") { it.time.toString() }
    }

    @TypeConverter
    fun toDateList(dateString: String): List<Date> {
        val timestampStrings = dateString.split(",")
        return timestampStrings.map { Date(it.toLong()) }
    }
}
