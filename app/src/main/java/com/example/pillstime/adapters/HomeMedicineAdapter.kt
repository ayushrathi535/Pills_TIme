package com.example.pillstime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.databinding.MedicineCardBinding
import com.example.pillstime.model.Medicine

class HomeMedicineAdapter : RecyclerView.Adapter<HomeMedicineAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        Log.d("HomeMedicineAdapter", "onCreateViewHolder")
        val binding = MedicineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("HomeMedicineAdapter", "getItemCount: ${differ.currentList.size}")
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        Log.d("HomeMedicineAdapter", "onBindViewHolder at position: $position")
        val medItem = differ.currentList[position]
        holder.bind(medItem)
    }

    inner class MedicineViewHolder(private val binding: MedicineCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medItem: Medicine?) {
            Log.d("HomeMedicineAdapter", "bind at position: $adapterPosition")
            binding.medicineName.text = medItem?.medicineName
            binding.quantity.text = medItem?.amount.toString()
            binding.medicineType.text = medItem?.medicineType
        //    binding.medicineTime.text = medItem?.doseTimes.toString()
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
