package com.example.pillstime.ui

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillstime.R
import com.example.pillstime.adapters.TimeAdapter
import com.example.pillstime.databinding.FragmentMedicineTimeBinding
import com.example.pillstime.model.DoseTime
import com.example.pillstime.roomdb.MedicineDatabase
import com.example.pillstime.utils.selectTime
import com.example.pillstime.utils.showNumberPicker
import com.example.pillstime.viewmodels.MedicineViewModel
import com.example.pillstime.viewmodels.MedicineViewModelFactory
import kotlinx.coroutines.launch


class MedicineTimeFragment : Fragment(), TimeAdapter.OnTimeItemClickListener {
    private var _binding: FragmentMedicineTimeBinding? = null
    private val binding get() = _binding!!

    private var defaultNum = 2

    private lateinit var adapter: TimeAdapter

    private  var timeList: MutableList<DoseTime> = mutableListOf()
    private var medicineId: String? = null

    private lateinit var viewModel: MedicineViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMedicineTimeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val medId = arguments?.getSerializable("medicineID")


        val medicineDao = MedicineDatabase.getInstance(requireContext()).medicineDao()
        val viewModelFactory = MedicineViewModelFactory(medicineDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MedicineViewModel::class.java)



        if (medId != null) {

            medicineId = medId.toString()
            viewLifecycleOwner.lifecycleScope.launch {

                val medicine = viewModel.getMedicineById(medicineId!!.toLong())

                if (medicine != null) {
                    timeList = medicine.doseTimes!!.toMutableList()
                }
            }
        }



        setupToolbar()

        setupClickEvent()

        if (timeList.isEmpty()) {
            timeList = MutableList(defaultNum) { generateRandomDoseTime() }
        }

        setUpTimeRecycler(timeList)
    }


    private fun setUpTimeRecycler(timeList: List<DoseTime>) {
        adapter = TimeAdapter(this)
        adapter.differ.submitList(timeList)

        binding.timeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@MedicineTimeFragment.adapter
        }
    }

    private fun setupClickEvent() {
        binding.number.text = defaultNum.toString()
        binding.medInterval.setOnClickListener {
            showNumberPicker(requireActivity()) { number ->
                defaultNum = number
                binding.number.text = defaultNum.toString()
                updateTimeListSize(defaultNum)
            }
        }

        binding.saveBtn.setOnClickListener {

//            val result = Bundle().apply {
//                putParcelableArrayList("doseTimeList", ArrayList(timeList))
//            }
            //   parentFragmentManager.setFragmentResult("requestKey", result)

            val result = Bundle().apply {
                putSerializable("doseTimeList", ArrayList(timeList))
            }
            parentFragmentManager.setFragmentResult("requestKey", result)

            requireActivity().supportFragmentManager.popBackStack()


            Log.e("final time list--->", "${timeList}: size ${timeList.size}")
        }
    }

    private fun updateTimeListSize(newSize: Int) {

        Log.e("list size--->", newSize.toString())
        timeList.clear()
        repeat(newSize) {
            timeList.add(generateRandomDoseTime())
        }
        adapter.differ.submitList(timeList.toList())
        adapter.notifyDataSetChanged()
    }


    private fun setupToolbar() {
        with(binding.toolbar.toolbar) {

            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        with(binding.toolbar.toolbarText) {

            text = "Intake Plan"

            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
        }

        with(binding.toolbar.icon) {
            visibility = View.VISIBLE
            setOnClickListener {
                // Todo cross icon click
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int, doseTime: DoseTime) {
        selectTime(requireActivity()) { selectedTime ->
            Log.e("position-->", position.toString())
            timeList[position] = selectedTime
            adapter.differ.submitList(timeList.toList())
            adapter.notifyItemChanged(position)
            Log.e("med-time-->", "time is $selectedTime")
        }
    }

    private fun generateRandomDoseTime(): DoseTime {
        val randomHour = (0..12).random().toString().padStart(2, '0')
        val randomMinute = (0..30).random().toString().padStart(2, '0')
        val randomAmPm = "PM"
        //   if ((0..1).random() == 0) "AM" else"PM"
        return DoseTime(randomHour, randomMinute, randomAmPm)
    }
}