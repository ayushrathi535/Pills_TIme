package com.example.pillstime.utils

import androidx.room.TypeConverter
import com.example.pillstime.model.DoseTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<DoseTime>? {
        val listType = object : TypeToken<List<DoseTime>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<DoseTime>?): String? {
        return Gson().toJson(list)
    }


        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return value?.let { Date(it) }
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }


}
