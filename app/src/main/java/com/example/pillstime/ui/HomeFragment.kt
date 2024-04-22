package com.example.pillstime.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillstime.R
import com.example.pillstime.adapters.HomeCalendarAdapter
import com.example.pillstime.adapters.HomeMedicineAdapter
import com.example.pillstime.databinding.FragmentHomeBinding
import com.example.pillstime.model.CalendarModel
import com.example.pillstime.model.DoseTime
import com.example.pillstime.interfaces.DataPassListener
import com.example.pillstime.model.Medicine
import com.example.pillstime.roomdb.MedicineDatabase
import com.example.pillstime.utils.DateUtils
import com.example.pillstime.viewmodels.MedicineViewModel
import com.example.pillstime.viewmodels.MedicineViewModelFactory
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment(), HomeCalendarAdapter.OnDateItemClickListener {


    private val calendar = Calendar.getInstance()
    private var currentMonth = 0

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HomeCalendarAdapter
    private lateinit var medAdapter: HomeMedicineAdapter
    private lateinit var viewModel: MedicineViewModel

    //total med
    private val selectedMedicines = mutableListOf<Medicine>()

    // med on selected date
    private val currentMedicines = mutableListOf<Medicine>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("fragment--->", "homeFragment")


        val medicineDao = MedicineDatabase.getInstance(requireContext()).medicineDao()
        val viewModelFactory = MedicineViewModelFactory(medicineDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MedicineViewModel::class.java)


        viewModel.medList.observe(viewLifecycleOwner) { medList ->
            Log.e("home fragment getList---> ", "${medList.size}")

            selectedMedicines.clear()
            selectedMedicines.addAll(medList)
            Log.e("selected med observe --->",selectedMedicines.size.toString())
        }


        setupHomeMedicineRecycler(currentMedicines)
        binding.monthNdYear.text = currentMonth.toString()


        initializeFab()
        // Set up RecyclerView with calendarList
        setUpCalendarRecycler(getFutureDatesOfCurrentMonth())

        onLeftRightButtonClick()
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


    private fun onLeftRightButtonClick() {
        binding.btnLeft.setOnClickListener {
            adapter.getCalendarList(getDatesOfPreviousMonth())
        }

        binding.btnRight.setOnClickListener {
            adapter.getCalendarList(getDatesOfNextMonth())
        }
    }

    private fun setUpCalendarRecycler(calendarList: List<Date>) {
        if (calendarList.isNotEmpty()) {
            adapter = HomeCalendarAdapter(this)
            adapter.differ.submitList(calendarList)

            binding.calendarView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = this@HomeFragment.adapter
            }
        } else {
            // Handle empty list case
            Log.e("HomeFragment", "Calendar list is empty")
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
        val activity = activity as MainActivity
        activity?.let {
            it.getBottomNavigationView().visibility = View.VISIBLE
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(date: Date) {
        binding.monthNdYear.text = "${DateUtils.getMonthName(date)} ${DateUtils.getYear(date)}"

        currentMedicines.clear() // Clear the current list before adding new medicines
        for (medicine in selectedMedicines) {
            if (isDateInRange(date, medicine.startMedDate, medicine.endMedDate)) {
                currentMedicines.add(medicine) // matching medicine  to currentMedicines

            }
        }
        Log.e("onItem Click-->", "size of current med is ${currentMedicines.size.toString()}")

        if (currentMedicines.size>0) {
            binding.medicineRecycler.visibility=View.VISIBLE
            binding.emptyPlaceholder.visibility=View.GONE
            medAdapter.differ.submitList(currentMedicines.toList())
        }
        else{
            binding.medicineRecycler.visibility=View.GONE
            binding.emptyPlaceholder.visibility=View.VISIBLE
        }
        Log.e("onItem Click-->", "list pass to differ is${currentMedicines.toList()}")
    }



    private fun isDateInRange(date: Date, startDate: Date?, endDate: Date?): Boolean {
        if (startDate != null && endDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val selectedTimeInMillis = calendar.timeInMillis

            calendar.time = startDate
            val startTimeInMillis = calendar.timeInMillis

            calendar.time = endDate
            val endTimeInMillis = calendar.timeInMillis

            Log.e("isDateInRange--->","$selectedTimeInMillis $startTimeInMillis $endTimeInMillis")
            return selectedTimeInMillis in startTimeInMillis..endTimeInMillis
        }
        return false
    }
}

