package com.example.pillstime.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pillstime.R
import com.example.pillstime.databinding.FragmentAddMedicineBinding
import com.example.pillstime.model.Days
import com.example.pillstime.model.DoseTime
import com.example.pillstime.model.Medicine
import com.example.pillstime.model.Reminder
import com.example.pillstime.model.ReminderState
import com.example.pillstime.model.Weeks
import com.example.pillstime.interfaces.ReminderCallback
import com.example.pillstime.notifications.AlarmReceiver
import com.example.pillstime.roomdb.MedicineDatabase
import com.example.pillstime.utils.DateUtils
import com.example.pillstime.utils.reminder
import com.example.pillstime.utils.selectDays
import com.example.pillstime.utils.showDateDialog
import com.example.pillstime.viewmodels.MedicineViewModel
import com.example.pillstime.viewmodels.MedicineViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.minutes


class AddMedicineFragment : Fragment(), ReminderCallback {

    private var _binding: FragmentAddMedicineBinding? = null
    private val binding get() = _binding!!


    private var amount: Int = 0
    private var selectedMedicineType: String? = null
    private var reminderData: Reminder = Reminder()
    private var medName: String? = null
    private var startDate: Date? = null
    private var endDate: Date? = null

    //flag to check if user select any option fom day dialog or not
    private var DAYS_FLAG: Boolean? = null
    private var weeks = Weeks()
    private var resultDays: String? = null
    private var timeList: MutableList<DoseTime> = mutableListOf()

    private var medicineID: String? = null

    private lateinit var fakeTimeList: MutableList<DoseTime>

    // to check that time list is null or not
    private var timeListInitialized = false


    private lateinit var viewModel: MedicineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddMedicineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val medicineDao = MedicineDatabase.getInstance(requireContext()).medicineDao()
        val viewModelFactory = MedicineViewModelFactory(medicineDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MedicineViewModel::class.java)

        fakeTimeList = mutableListOf(
            DoseTime("10", "00", "AM"),
            DoseTime("02", "00", "PM"),
        )

        receieveListFromTimeFragment()

        setupMedTime()

        setupDays()

        setupToolbar()

        setupClicks()



        binding.saveBtn.setOnClickListener {

            medName = binding.medName.text.toString()

            if (medName.isNullOrEmpty() || amount == 0 || startDate?.date == null ||
                endDate?.date == null || resultDays.isNullOrEmpty() || DAYS_FLAG == true ||
                timeList.size <= 0
            ) {
                Log.d("AddMedicineFragment", "Checking conditions for saving medicine:")
                Log.d(
                    "AddMedicineFragment",
                    "Medicine name isNullOrEmpty: ${medName.isNullOrEmpty()}"
                )
                Log.d("AddMedicineFragment", "Amount is zero: ${amount == 0}")
                Log.d("AddMedicineFragment", "Start date is not null: ${startDate?.date}")
                Log.d("AddMedicineFragment", "End date enddate is not null: ${endDate?.date}")
                Log.d(
                    "AddMedicineFragment",
                    "Result days isNullOrEmpty: ${resultDays.isNullOrEmpty()}"
                )
                Log.d("AddMedicineFragment", "DAYS_FLAG is true: ${DAYS_FLAG == true}")
                Log.d(
                    "AddMedicineFragment",
                    "Time list size is greater than 0: ${timeList.size > 0}"
                )

                Log.e("saveBTN-->", "if block")
                Toast.makeText(requireContext(), "enter all fields", Toast.LENGTH_LONG).show()

            } else {
                val medicine = Medicine(
                    medicineName = medName,
                    medicineType = selectedMedicineType,
                    amount = amount,
                    startMedDate = startDate,
                    endMedDate = endDate,
                    doseDays = weeks,
                    doseTimes = timeList,
                    reminder = reminderData
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        viewModel.insertMedicine(medicine)
                    } catch (e: Exception) {
                        Log.e("AddMedicineFragment", "Error inserting medicine", e)
                        Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                scheduleNotificationsForMedicine(medicine)
                requireActivity().supportFragmentManager.popBackStack()
            }

            Log.e(
                "output---->", "$medName, $amount $selectedMedicineType $reminderData" +
                        "$startDate $endDate ${weeks} ${timeList} "
            )

        }

        Log.e("fragment--->", "addMedicineFragment")
        val activity = activity as MainActivity

        activity?.let {
            it.getBottomNavigationView().visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        var startDates: String? = null
        var endDates: String? = null
        if (startDate != null) {

            startDates =
                DateUtils.formatDate(startDate?.year!!, startDate?.month!!, startDate?.date!!)
        }
        if (endDate != null) {
            endDates =
                DateUtils.formatDate(endDate?.year!!, endDate?.month!!, endDate?.date!!)
        }

        if (startDates != null) {
            binding.startDate.text = startDates
        }
        if (endDates != null) {
            binding.endDate.text = endDates
        }
        if (amount != 0) {
            binding.amount.text = amount.toString()
        }


        setupReminderText(reminderData)

        Log.e("ADDMEDFRAG-->", "onResume-->")
    }

    private fun receieveListFromTimeFragment() {


        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            // Retrieve serializable objects from the bundle
            val receivedList = bundle.getSerializable("doseTimeList") as? ArrayList<DoseTime>

            receivedList?.let {
                timeList = it.toMutableList()
                timeListInitialized = true
                setupMedTime()
            }
        }


//        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
//            val receivedList = bundle.getParcelableArrayList<DoseTime>("doseTimeList")
//
////            // Retrieve the serialized string from the result bundle
////            val timeListJson = result.getString("doseTimeListJson")
////
////// Deserialize the string into a list of DoseTime objects
////            val timeListType = object : TypeToken<List<DoseTime>>() {}.type
////            val timeList: List<DoseTime> = Gson().fromJson(timeListJson, timeListType)
//
//
//
//            receivedList?.let {
//                timeList = it.toMutableList()
//                timeListInitialized = true
//                setupMedTime()
//            }
//        }
    }

    private fun setupDays() {

        binding.days.text = "Select days "
        Log.e("weeks-->", weeks.toString())
    }


    private fun setupMedTime() {

        if (
            timeListInitialized
        ) {
            val formattedTimeList = buildString {
                timeList.forEachIndexed { index, doseTime ->
                    if (index > 0 && index % 2 == 0) {
                        append("\n")
                    } else if (index > 0) {
                        append(",  ")
                    }
                    append("${doseTime.hour}:${doseTime.min} ${doseTime.am_pm}")
                }
            }
            binding.medTime.text = formattedTimeList

        } else {
            binding.medTime.text = "Select Time For Med"
        }

        // binding.medTime.text = formattedTimeList
//
//       binding.medTime.text = "Select Time For Med"
    }


    private fun setupClicks() {

        Log.e("reminder data--->", reminderData.toString())

        binding.reminder.setOnClickListener {

            reminder(requireActivity(), reminderData, callback = this)

        }

        binding.days.setOnClickListener {


            selectDays(requireActivity(),
                onNoDaysSelected = { noDaysSelected ->
                    //  if false  then user selct for true not selct any
                    DAYS_FLAG = noDaysSelected
                    Log.e("days_flag--->", DAYS_FLAG.toString())
                },
                listener = { selectedDays ->

                    onDaysSelected(selectedDays)
                }

            )

        }

        binding.medTime.setOnClickListener {

            loadFragment(MedicineTimeFragment())

        }

        binding.startDate.setOnClickListener {
            showDateDialog(requireActivity(), "Starting Date") { date ->

                val selectedDate = DateUtils.formatDate(date.year!!, date.month!!, date.date!!)

                binding.startDate.text = selectedDate
                startDate = date
            }
        }
        binding.endDate.setOnClickListener {
            showDateDialog(requireActivity(), "End Date") { date ->

                val selectedDate = DateUtils.formatDate(date.year!!, date.month!!, date.date!!)
                binding.endDate.text = selectedDate
//                val date = EndMedDate(date.date, date.month, date.year)
                endDate = date
            }
        }

        binding.decrease.setOnClickListener {
            if (amount != 0) {
                this.amount -= 1
                binding.amount.text = amount.toString()
            }
        }
        binding.increase.setOnClickListener {
            if (amount >= 0) {
                this.amount += 1
                binding.amount.text = amount.toString()
            }
        }


        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.medicine_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.medTypeSpinner.adapter = adapter
        }

        binding.medTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedMedicineType = parent?.getItemAtPosition(position) as String

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle nothing selected
                }
            }

    }


    private fun setupToolbar() {
        with(binding.toolbarLayout.toolbar) {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        with(binding.toolbarLayout.toolbarText) {
            text = "Add Medicine"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
        }

        with(binding.toolbarLayout.icon) {
            visibility = View.VISIBLE
            setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
//                    .commit()
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        val args = Bundle()
        // args.putParcelableArrayList("doseTimeList", ArrayList(timeList))
        args.putString("medicineID", medicineID)
        fragment.arguments = args

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction.add(R.id.fragContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onReminderStateSelected(reminderState: List<ReminderState>) {

        val vibration = reminderState.contains(ReminderState.VIBRATION)
        val notification = reminderState.contains(ReminderState.NOTIFICATION)
        val sound = reminderState.contains(ReminderState.SOUND)
        val all = reminderState.contains(ReminderState.ALL)

        reminderData = Reminder(
            vibration = all || vibration,
            notification = all || notification,
            soundSignal = all || sound,
            allType = all
        )

        setupReminderText(reminderData)
    }

    private fun setupReminderText(reminderData: Reminder) {

        val reminderStringBuilder = StringBuilder()
        if (reminderData.allType!!) {
            reminderStringBuilder.append("Default: ALL")
        } else {
            if (reminderData.vibration!!) {
                reminderStringBuilder.append("Vibration \n")
            }
            if (reminderData.notification!!) {
                reminderStringBuilder.append("Notification \n")
            }
            if (reminderData.soundSignal!!) {
                reminderStringBuilder.append("Sound \n")
            }
            if (reminderStringBuilder.isNotEmpty()) {
                reminderStringBuilder.delete(
                    reminderStringBuilder.length - 2,
                    reminderStringBuilder.length
                )
            }
        }

        binding.reminder.text = reminderStringBuilder.toString()

        Log.e("reminderData--->", reminderData.toString())
    }


    fun mapSelectedDaysToWeeks(selectedDays: List<Days>): Weeks {

        clearWeeksDays()

        for (day in selectedDays) {
            when (day.day) {
                "Sun" -> weeks.sunday = true
                "Mon" -> weeks.monday = true
                "Tue" -> weeks.tuesday = true
                "Wed" -> weeks.wednesday = true
                "Thu" -> weeks.thursday = true
                "Fri" -> weeks.friday = true
                "Sat" -> weeks.saturday = true
            }
        }


        weeks.allDays = weeks.sunday && weeks.monday && weeks.tuesday && weeks.wednesday &&
                weeks.thursday && weeks.friday && weeks.saturday

        return weeks

    }

    fun onDaysSelected(selectedDays: List<Days>) {

        weeks = mapSelectedDaysToWeeks(selectedDays)

        Log.e("Weeks instance -->", weeks.toString())
        val stringBuilder = StringBuilder()

        if (weeks.allDays) {
            stringBuilder.append("ALL Days")
        } else {
            if (weeks.sunday) {
                stringBuilder.append("Sunday\n")
            }
            if (weeks.monday) {
                stringBuilder.append("Monday\n")
            }
            if (weeks.tuesday) {
                stringBuilder.append("Tuesday\n")
            }
            if (weeks.wednesday) {
                stringBuilder.append("Wednesday\n")
            }
            if (weeks.thursday) {
                stringBuilder.append("Thursday\n")
            }
            if (weeks.friday) {
                stringBuilder.append("Friday\n")
            }
            if (weeks.saturday) {
                stringBuilder.append("Saturday\n")
            }

        }

        resultDays = stringBuilder.toString().trim()
        println("Result: $resultDays")

        setupDayText()

    }

    private fun setupDayText() {
        resultDays?.let { daysText ->
            binding.days.text = daysText
        }
    }

    private fun clearWeeksDays() {
        weeks.sunday = false
        weeks.monday = false
        weeks.tuesday = false
        weeks.wednesday = false
        weeks.thursday = false
        weeks.friday = false
        weeks.saturday = false
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotificationsForMedicine(medicine: Medicine) {

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity(), AlarmReceiver::class.java)

        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            else -> FLAG_UPDATE_CURRENT
        }
//        val pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent,
//            flags)

        val startDate = medicine.startMedDate ?: return
        val endDate = medicine.endMedDate ?: return
        val doseTimes = medicine.doseTimes ?: return


        val doseCalendar = Calendar.getInstance()
        doseCalendar.time = startDate

        while (doseCalendar.time <= endDate) {

            for (index in doseTimes.indices) {

                val calendar = Calendar.getInstance()
                calendar.time = doseCalendar.time

                Log.e("calendar-->", index.toString())

                val hour = doseTimes[index].hour.toInt()
                val minute = doseTimes[index].min.toInt()


                if (doseTimes[index].am_pm.equals("PM", ignoreCase = true) && hour < 12) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour + 12)
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                }

                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                Log.e("add fragment -->", "${calendar.time}  + $index ")

                val requestCode = index

                val pendingIntent = PendingIntent.getBroadcast(
                    requireActivity(), requestCode, intent,
                    flags
                )


                // Schedule the alarm for the dose time 9205861005
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            doseCalendar.add(Calendar.DAY_OF_MONTH, 1)

            Log.e("doseCalendar-->", doseCalendar.time.toString())
        }

    }

}


