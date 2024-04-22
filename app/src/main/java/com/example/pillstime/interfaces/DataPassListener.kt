package com.example.pillstime.interfaces

import com.example.pillstime.model.DoseTime

interface DataPassListener {
    fun onDataPassed(data: List<DoseTime>)
}