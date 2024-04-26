package com.example.pillstime.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.pillstime.R
import com.example.pillstime.adapters.CalendarAdapter
import com.example.pillstime.adapters.HomeMedicineAdapter
import com.example.pillstime.databinding.FragmentHomeBinding
import com.example.pillstime.model.CalendarDateModel
import com.example.pillstime.model.Medicine
import com.example.pillstime.roomdb.MedicineDatabase
import com.example.pillstime.utils.DateUtils
import com.example.pillstime.utils.HorizontalItemDecoration
import com.example.pillstime.viewmodels.MedicineViewModel
import com.example.pillstime.viewmodels.MedicineViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var medAdapter: HomeMedicineAdapter
    private lateinit var viewModel: MedicineViewModel

    //total med
    private val selectedMedicines = mutableListOf<Medicine>()

    // med on selected date
    private val currentMedicines = mutableListOf<Medicine>()

    //calendar adapter below
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpAdapter()
        setUpClickListener()
        setUpCalendar()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("home fragment--->", "onViewCreated called")


        val medicineDao = MedicineDatabase.getInstance(requireContext()).medicineDao()
        val viewModelFactory = MedicineViewModelFactory(medicineDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MedicineViewModel::class.java)


        viewModel.medList.observe(viewLifecycleOwner) { medList ->
            Log.e("home fragment getList---> ", "${medList.size}")

            selectedMedicines.clear()
            selectedMedicines.addAll(medList)
            Log.e("selected med observe --->", selectedMedicines.size.toString())
        }


        setupHomeMedicineRecycler(currentMedicines)

        initializeFab()


    }

    private fun setUpAdapter() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.single_calendar_margin)
        binding.calendarView.addItemDecoration(HorizontalItemDecoration(spacingInPixels))
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarView)

        adapter = CalendarAdapter{ calendarDateModel: CalendarDateModel, position: Int ->

            Log.e("position-->",position.toString())
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)

            Log.e("current date-->","${currentDate.time}")

            val date = calendarDateModel.data

                checkDateWithData(date)

        }

        binding.calendarView.adapter = adapter
    }



    private  fun checkDateWithData(date: Date){
        currentMedicines.clear() // Clear the current list before adding new medicines
        for (medicine in selectedMedicines) {
            if (isDateInRange(date, medicine.startMedDate, medicine.endMedDate)) {


                for (i in 0..5){
                    val day = DateUtils.getDayName(date)

                    Log.e("days---->",day)
                }

                currentMedicines.add(medicine) // adding medicine  to currentMedicines
            }

        }

        Log.e("onItem Click-->", "size of current med is ${currentMedicines.size.toString()}")

        if (currentMedicines.size > 0) {
            binding.medicineRecycler.visibility = View.VISIBLE
            binding.emptyPlaceholder.visibility = View.GONE
            medAdapter.differ.submitList(currentMedicines.toList())
        } else {
            binding.medicineRecycler.visibility = View.GONE
            binding.emptyPlaceholder.visibility = View.VISIBLE
        }


    }

    private fun setUpCalendar() {

        val calendarList = ArrayList<CalendarDateModel>()
        val monthAndYear=sdf.format(cal.time)
        binding.monthNdYear.text = monthAndYear
        binding.selectedDate.text=monthAndYear
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarList2.clear()
        calendarList2.addAll(calendarList)
        adapter.setData(calendarList)
    }


    private fun setUpClickListener() {
        binding.btnRight.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.btnLeft.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }
    }

    private fun setupHomeMedicineRecycler(medList: MutableList<Medicine>) {
        Log.e("setupHomeMedicineRecycler-->", medList.size.toString())
        medAdapter = HomeMedicineAdapter()
        medAdapter.differ.submitList(medList.toList())

        binding.medicineRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = medAdapter
        }
    }


    private fun initializeFab() {
        binding.fab.setOnClickListener {
            loadFragment(AddMedicineFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction.replace(R.id.fragContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    override fun onResume() {
        super.onResume()
        Log.e("home fragment--->", "onResume called")
        val activity = activity as MainActivity
        activity?.let {
            it.getBottomNavigationView().visibility = View.VISIBLE
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


//    @SuppressLint("SetTextI18n")
//    override fun onItemClick(date: Date) {
//        binding.monthNdYear.text = "${DateUtils.getMonthName(date)} ${DateUtils.getYear(date)}"
//
//        currentMedicines.clear() // Clear the current list before adding new medicines
//        for (medicine in selectedMedicines) {
//            if (isDateInRange(date, medicine.startMedDate, medicine.endMedDate)) {
//                currentMedicines.add(medicine) // matching medicine  to currentMedicines
//
//            }
//        }
//        Log.e("onItem Click-->", "size of current med is ${currentMedicines.size.toString()}")
//
//        if (currentMedicines.size>0) {
//            binding.medicineRecycler.visibility=View.VISIBLE
//            binding.emptyPlaceholder.visibility=View.GONE
//            medAdapter.differ.submitList(currentMedicines.toList())
//        }
//        else{
//            binding.medicineRecycler.visibility=View.GONE
//            binding.emptyPlaceholder.visibility=View.VISIBLE
//        }
//        Log.e("onItem Click-->", "list pass to differ is${currentMedicines.toList()}")
//    }


    private fun isDateInRange(date: Date, startDate: Date?, endDate: Date?): Boolean {
        if (startDate != null && endDate != null) {
            val calendar = Calendar.getInstance()

            calendar.time = date

            val selectedTimeInMillis = calendar.timeInMillis

            calendar.time = startDate
            val startTimeInMillis = calendar.timeInMillis

            calendar.time = endDate
            val endTimeInMillis = calendar.timeInMillis

            // val isInRange = selectedTimeInMillis in startTimeInMillis..endTimeInMillis


            val selectedDateString =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedTimeInMillis)
            val startDateString =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startTimeInMillis)
            val endDateString =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endTimeInMillis)

            val isInRange = selectedDateString in startDateString..endDateString


            Log.d("isDateInRange--->", "Selected Date: $selectedDateString")
            Log.d("isDateInRange--->", "Start Date: $startDateString")
            Log.d("isDateInRange--->", "End Date: $endDateString")
            Log.d("isDateInRange--->", "Is in Range: $isInRange")

            return isInRange
        }
        return false
    }

}

