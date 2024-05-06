package com.example.pillstime.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillstime.R
import com.example.pillstime.adapters.MyMedicineAdapter
import com.example.pillstime.databinding.FragmentMedicineBinding
import com.example.pillstime.model.Medicine
import com.example.pillstime.roomdb.MedicineDatabase
import com.example.pillstime.viewmodels.MedicineViewModel
import com.example.pillstime.viewmodels.MedicineViewModelFactory

class MedicineFragment : Fragment() {


    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!

    private val selectedMedicines = mutableListOf<Medicine>()

    private lateinit var viewModel: MedicineViewModel
    private lateinit var medAdapter: MyMedicineAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMedicineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupToolbar()
        setUpViewModel()
        setUpAdapter()

    }

    private fun setUpViewModel() {
        val medicineDao = MedicineDatabase.getInstance(requireContext()).medicineDao()
        val viewModelFactory = MedicineViewModelFactory(medicineDao)
        viewModel = ViewModelProvider(this, viewModelFactory)[MedicineViewModel::class.java]

        viewModel.medList.observe(viewLifecycleOwner) { medicineList ->

            selectedMedicines.clear()
            selectedMedicines.addAll(medicineList)
            Log.e("my med list-->",selectedMedicines.size.toString())
            if (selectedMedicines.isEmpty()) {
                setViewHolder(true)
            }
            else{
                setViewHolder(false)
                medAdapter.differ.submitList(this.selectedMedicines)
                medAdapter.notifyDataSetChanged()
            }

        }

    }

    private fun setViewHolder(flag: Boolean) {

        if (flag) {
            binding.emptyPlaceholder.visibility = View.VISIBLE
            binding.myMedRecyler.visibility = View.GONE
        }
        else{
            binding.emptyPlaceholder.visibility = View.GONE
            binding.myMedRecyler.visibility = View.VISIBLE
        }
    }

    private fun setUpAdapter() {

         medAdapter = MyMedicineAdapter()
        binding.myMedRecyler.apply {

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = medAdapter
        }
        Log.e("setUpAdapter-->",selectedMedicines.size.toString())
        medAdapter.differ.submitList(this.selectedMedicines)
       // medAdapter.notifyDataSetChanged()
    }
    private fun setupToolbar() {
        with(binding.toolbar.toolbar) {

            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        with(binding.toolbar.toolbarText) {

            text = "My Medicines"

            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}