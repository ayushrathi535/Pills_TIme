package com.example.pillstime.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.databinding.MyMedicineCardBinding
import com.example.pillstime.model.Medicine
import com.example.pillstime.utils.DateUtils

class MyMedicineAdapter : RecyclerView.Adapter<MyMedicineAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MyMedicineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val medItem = differ.currentList[position]
        holder.bind(medItem)
    }

    inner class ViewHolder(private val binding: MyMedicineCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medItem: Medicine?) {
            binding.apply {
                medicineName.text=medItem?.medicineName
                val startDate=medItem?.startMedDate
                val endDate=medItem?.endMedDate

                fromDate.text = "${DateUtils.getDayNumber(startDate!!)} ${DateUtils.getMonthName(startDate!!)}-"
                toDate.text = "${DateUtils.getDayNumber(endDate!!)} ${DateUtils.getMonthName(endDate!!)}"


            }
        }

    }


    private val differCallback = object : DiffUtil.ItemCallback<Medicine>() {
        override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)


}