package com.example.pillstime.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pillstime.model.Medicine


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

}