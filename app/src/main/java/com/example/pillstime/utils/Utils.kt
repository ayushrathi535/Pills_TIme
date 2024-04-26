package com.example.pillstime.utils

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CalendarView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.adapters.DialogDayAdapter
import com.example.pillstime.model.Days
import com.example.pillstime.model.DoseTime
import com.example.pillstime.model.Reminder
import com.example.pillstime.model.ReminderState
import com.example.pillstime.interfaces.ReminderCallback
import java.util.Calendar
import java.util.Date



var dialogInstance: Dialog? = null
fun hideKeyboard(context: Context, view: View?) {
    try {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun getReminderState(
    soundState: Boolean,
    notificationState: Boolean,
    vibrationState: Boolean,
    allState: Boolean
): List<ReminderState> {
    val selectedStates = mutableListOf<ReminderState>()

    if (allState) {
        selectedStates.add(ReminderState.ALL)
    } else {
        if (soundState) {
            selectedStates.add(ReminderState.SOUND)
        }
        if (notificationState) {
            selectedStates.add(ReminderState.NOTIFICATION)
        }
        if (vibrationState) {
            selectedStates.add(ReminderState.VIBRATION)
        }
    }
    if (soundState && notificationState && vibrationState) {
        selectedStates.add(ReminderState.ALL)
    }

    if (selectedStates.isEmpty()) {
        selectedStates.add(ReminderState.ALL)
    }

    return selectedStates
}


fun reminder(context: Context, reminder: Reminder, callback: ReminderCallback?) {


    var soundState = reminder.soundSignal
    var notificationState = reminder.notification
    var vibrationState = reminder.vibration
    var allState = reminder.allType


    val dialog: Dialog

    if (dialogInstance != null && dialogInstance!!.isShowing) {
        return
    } else {
        dialog = Dialog(context, R.style.MyAlertDialogTheme)
        dialogInstance = dialog
    }

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.reminder_card)

    val ok = dialog.findViewById<TextView>(R.id.ok)
    val cancel = dialog.findViewById<TextView>(R.id.cancel)
    val sound = dialog.findViewById<TextView>(R.id.soundSignal)
    val notification = dialog.findViewById<TextView>(R.id.notification)
    val vibration = dialog.findViewById<TextView>(R.id.vibration)
    val all = dialog.findViewById<TextView>(R.id.chooseAll)

    dialog.show()
    dialog.getWindow()?.setBackgroundDrawableResource(R.color.transparent);

    Log.e("allState", "allState is $allState")

    cancel.setOnClickListener {
        dialog.dismiss()
    }

    ok.setOnClickListener {
        val reminderState =
            getReminderState(soundState!!, notificationState!!, vibrationState!!, allState!!)

        callback?.onReminderStateSelected(reminderState)

        dialog.dismiss()
    }

    if (
        allState == true
    ) {
        sound.setTextColor(ContextCompat.getColor(context, R.color.white))
        sound.setBackgroundResource(R.drawable.button_background)
        vibration.setTextColor(ContextCompat.getColor(context, R.color.white))
        vibration.setBackgroundResource(R.drawable.button_background)
        notification.setTextColor(ContextCompat.getColor(context, R.color.white))
        notification.setBackgroundResource(R.drawable.button_background)
    } else {
        setButtonState(context, sound, soundState!!)
        setButtonState(context, vibration, vibrationState!!)
        setButtonState(context, notification, notificationState!!)
    }

    sound.setOnClickListener {
        soundState = !soundState!!
        if (soundState!!) {
            sound.setTextColor(ContextCompat.getColor(context, R.color.white))
            sound.setBackgroundResource(R.drawable.button_background)
        } else {
            sound.setTextColor(ContextCompat.getColor(context, R.color.brand_color))
            sound.setBackgroundResource(R.drawable.button_background_light)
            allState = false
        }
    }

    vibration.setOnClickListener {
        vibrationState = !vibrationState!!
        if (vibrationState!!) {
            vibration.setTextColor(ContextCompat.getColor(context, R.color.white))
            vibration.setBackgroundResource(R.drawable.button_background)
        } else {
            vibration.setTextColor(ContextCompat.getColor(context, R.color.brand_color))
            vibration.setBackgroundResource(R.drawable.button_background_light)
            allState = false
        }
    }

    notification.setOnClickListener {
        notificationState = !notificationState!!
        if (notificationState!!) {
            notification.setTextColor(ContextCompat.getColor(context, R.color.white))
            notification.setBackgroundResource(R.drawable.button_background)
        } else {
            notification.setTextColor(ContextCompat.getColor(context, R.color.brand_color))
            notification.setBackgroundResource(R.drawable.button_background_light)
            allState = false
        }
    }


    all.setOnClickListener {

        if (!allState!!) {
            sound.setTextColor(ContextCompat.getColor(context, R.color.white))
            sound.setBackgroundResource(R.drawable.button_background)
            vibration.setTextColor(ContextCompat.getColor(context, R.color.white))
            vibration.setBackgroundResource(R.drawable.button_background)
            notification.setTextColor(ContextCompat.getColor(context, R.color.white))
            notification.setBackgroundResource(R.drawable.button_background)
            allState = true
            soundState = true
            vibrationState = true
            notificationState = true
        } else {
            sound.setTextColor(ContextCompat.getColor(context, R.color.brand_color))
            sound.setBackgroundResource(R.drawable.button_background_light)
            vibration.setTextColor(ContextCompat.getColor(context, R.color.brand_color))
            vibration.setBackgroundResource(R.drawable.button_background_light)
            notification.setTextColor(ContextCompat.getColor(context, R.color.brand_color))
            notification.setBackgroundResource(R.drawable.button_background_light)
            soundState = false
            vibrationState = false
            notificationState = false
            allState = false
        }

    }

}

private fun setButtonState(context: Context, button: TextView, state: Boolean) {
    button.setTextColor(
        ContextCompat.getColor(
            context,
            if (state) R.color.white else R.color.brand_color
        )
    )
    button.setBackgroundResource(if (state) R.drawable.button_background else R.drawable.button_background_light)
}


fun selectDays(
    context: Context,
//    listener: DaySelectionListener?,
    onNoDaysSelected: (Boolean) -> Unit,
    listener : (List<Days>) ->Unit
) {


    val week = listOf(
        Days("Mon"),
        Days("Tue"),
        Days("Wed"),
        Days("Thu"),
        Days("Fri"),
        Days("Sat"),
        Days("Sun"),
    )

    val dialog: Dialog

    if (dialogInstance != null && dialogInstance!!.isShowing) {
        return
    } else {
        dialog = Dialog(context, R.style.MyAlertDialogTheme)
        dialogInstance = dialog
    }
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.custom_day_dialog)

    val daysRecyclerView = dialog.findViewById<RecyclerView>(R.id.days)
    val ok = dialog.findViewById<TextView>(R.id.ok)
    val cancel = dialog.findViewById<TextView>(R.id.cancel)
    val everyDay = dialog.findViewById<TextView>(R.id.chooseAll)

    val adapter = DialogDayAdapter(week)
    daysRecyclerView.layoutManager =
        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    daysRecyclerView.adapter = adapter

    cancel.setOnClickListener {
        dialog.dismiss()
    }

    ok.setOnClickListener {
        val selectedDays = adapter.getSelectedDays()
        dialog.dismiss()
        if (selectedDays.isEmpty()) {
            onNoDaysSelected(true)
        } else {
            listener(selectedDays)
           // listener?.onDaysSelected(selectedDays)
            onNoDaysSelected(false)
        }
        dialog.dismiss()
    }

    everyDay.setOnClickListener {
        adapter.everyDay()
    }

    dialog.show()
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
}


var dialogInstanc: Dialog? = null
fun showNumberPicker(context: Context, num: (Int) -> Unit) {

    var number: Int = 1

    val dialog: Dialog
    if (dialogInstanc != null && dialogInstanc!!.isShowing) {
        return
    } else {
        dialog = Dialog(context, R.style.MyAlertDialogTheme)
        dialogInstanc = dialog
    }

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(true)
    dialog.setContentView(R.layout.item_multiple_time)

    val picker = dialog.findViewById<NumberPicker>(R.id.numberPicker)
    val ok = dialog.findViewById<TextView>(R.id.ok)
    val cancel = dialog.findViewById<TextView>(R.id.cancel)

    picker.maxValue = 100
    picker.minValue = 1
    picker.wrapSelectorWheel = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val color = ContextCompat.getColor(context, R.color.brand_color)
        picker.textColor = color
    }

    picker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->

        number = newVal

    })


    cancel.setOnClickListener {
        dialog.dismiss()
    }

    ok.setOnClickListener {
        num(number)
        dialog.dismiss()
    }

    dialog.show()
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
//    val window = dialog.window
//    window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
}

fun selectTime(context: Context, onTimeSelected: (DoseTime) -> Unit) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.custom_time_picker)

    val timePicker = dialog.findViewById<TimePicker>(R.id.timePicker)
    val ok = dialog.findViewById<TextView>(R.id.ok)
    val cancel = dialog.findViewById<TextView>(R.id.cancel)

    val currentTime = Calendar.getInstance()

    timePicker.setIs24HourView(false)
    timePicker.hour = currentTime.get(Calendar.HOUR_OF_DAY)
    timePicker.minute = currentTime.get(Calendar.MINUTE)

    cancel.setOnClickListener { dialog.dismiss() }

    ok.setOnClickListener {
        val selectedHour = timePicker.hour
        val selectedMinute = timePicker.minute
        val amPm = if (selectedHour < 12) "AM" else "PM"
        val hour12Format =
            if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
        val formattedHour = String.format("%02d", hour12Format)
        val formattedMinute = String.format("%02d", selectedMinute)

        val doseTime = DoseTime(formattedHour, formattedMinute, amPm)
        onTimeSelected(doseTime)
        dialog.dismiss()
    }

    dialog.show()
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
}


fun showDateDialog(context: Context, title: String, onDateSelected: (Date) -> Unit) {

//    var selectedDate: String = ""
//    var selectedMedDate:Date?=null

    val dialog: Dialog
    if (dialogInstance != null && dialogInstance!!.isShowing) {
        return
    } else {
        dialog = Dialog(context, R.style.MyAlertDialogTheme)
        dialogInstance = dialog
    }

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.custom_date_picker_layout)

    val calendarView = dialog.findViewById<CalendarView>(R.id.datePicker)
    val ok = dialog.findViewById<TextView>(R.id.ok)
    val cancel = dialog.findViewById<TextView>(R.id.cancel)
    val headerTitle = dialog.findViewById<TextView>(R.id.dateHeader)
    headerTitle.text = title


    calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
        val selectedDate = Date(year - 1900, month, dayOfMonth)
        onDateSelected(selectedDate)
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }

    ok.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
//           val window = dialog.window
//           window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
}







