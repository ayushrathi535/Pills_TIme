package com.example.pillstime.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillstime.roomdb.MedicineDao

class MedicineViewModelFactory(private val medicineDao: MedicineDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicineViewModel(medicineDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
