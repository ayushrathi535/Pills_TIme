package com.example.pillstime.utils

import androidx.room.TypeConverter
import com.example.pillstime.model.CheckDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Date


class ConvertersTwo{


    @TypeConverter
    fun fromCheckDateList(value: List<CheckDate>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toCheckDateList(value: String?): List<CheckDate>? {
        val listType: Type? = object : TypeToken<List<CheckDate>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}

