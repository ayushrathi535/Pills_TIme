package com.example.pillstime.roomdb

import android.app.Application
import androidx.room.Room

class MyApp : Application() {

    companion object {
        lateinit var database: MedicineDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            MedicineDatabase::class.java,
            "medicine_database"
        ).build()
    }
}