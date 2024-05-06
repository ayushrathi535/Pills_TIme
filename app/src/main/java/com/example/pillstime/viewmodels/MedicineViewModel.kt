package com.example.pillstime.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillstime.model.Medicine
import com.example.pillstime.model.MedicineTrack
import com.example.pillstime.roomdb.MedicineDao
import kotlinx.coroutines.launch

class MedicineViewModel(private val medicineDao: MedicineDao) : ViewModel() {


//    private var _medList: MutableLiveData<List<Medicine>> = MutableLiveData()
//    val medList: LiveData<List<Medicine>> = _medList

    val medList: LiveData<List<Medicine>> = medicineDao.getAllMedicine()

    val trackList:LiveData<List<MedicineTrack>> =medicineDao.getAllDates()
//    init {
//        getAllMedicine()
//    }
//
//     private fun getAllMedicine() {
//         viewModelScope.launch {
//             val medicineList = medicineDao.getAllMedicine()
//             _medList.postValue(medicineList.value)
//         }




    suspend fun getMedicineById(id: Long): Medicine? {
        return medicineDao.getMedicine(id)
    }

    suspend fun getMedTrackById(id: Long): MedicineTrack? {
        return medicineDao.getMedTrackRecord(medID = id)
    }

    suspend fun insertMedTrackRecord(medicineTrack: MedicineTrack){
        return medicineDao.insertMedTakenRecord(medicineTrack)
    }


    suspend  fun insertMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineDao.insertMedReminder(medicine)
        }

    }

     fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineDao.updateMedicine(medicine)
        }
    }

    fun deleteMedicineByID(id: Long) {
        viewModelScope.launch {
            medicineDao.deleteMedicine(id)
        }
    }




}

