package com.example.pillstime.model


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pillstime.utils.Converters
import java.util.Date


@Entity(tableName = "my_medicine")
@TypeConverters(Converters::class)
data class Medicine(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var medicineName: String?,

    var medicineType:String?,

    var amount:Int?,


    var startMedDate :Date?=null,

    var endMedDate:Date?=null,

    @Embedded
    var doseDays: Weeks?=null,

//    val date:Date ?,

    var doseTimes : List<DoseTime>?=null,
    @Embedded
    var reminder: Reminder?=null
)


data class StartMedDate(
    var date:Int?,
    var month:Int?,
    var year:Int?
)

data class EndMedDate(
    var enddate:Int?,
    var endmonth:Int?,
    var endyear:Int?
)

data class DoseTime(
    var  hour:String,
    var min:String,
    var am_pm:String
)


data class  Reminder(
    var vibration:Boolean?=true,
    var notification:Boolean?=true,
    var soundSignal: Boolean?=true,
    var allType:Boolean ? =true
)

data class Weeks(
    var sunday: Boolean = false,
    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var allDays: Boolean = false
)


data class Days(
    val day:String
)



data class medDate(
    var date:Int?,
    var month:Int?,
    var year:Int?
)
//val Mo:String,
//    val Tu:String,
//    val Wed:String,
//    val Thur:String,
//    val Fri:String,
//    val Sat:String,
//    val Su:String