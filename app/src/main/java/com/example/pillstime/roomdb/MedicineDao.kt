package com.example.pillstime.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.pillstime.model.Medicine
import com.example.pillstime.model.MedicineTrack


@Dao
interface MedicineDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedReminder(medicine: Medicine)

    @Update
    suspend fun updateMedicine(medicine: Medicine)


    @Query("DELETE  from my_medicine Where id = :ID ")
    suspend fun deleteMedicine(ID:Long)

    @Query("Select * from my_medicine Where id = :ID ")
    suspend fun getMedicine(ID:Long):Medicine?


    @Query("Select * FROM my_medicine")
    fun getAllMedicine():LiveData<List<Medicine>>

    @Query("Select * FROM medicine_track")
    fun getAllDates():LiveData<List<MedicineTrack>>

    @Upsert
   suspend fun insertMedTakenRecord(medicineTrack: MedicineTrack)

   @Query("SELECT * from medicine_track where medId = :medID")
   suspend fun getMedTrackRecord(medID:Long):MedicineTrack

}