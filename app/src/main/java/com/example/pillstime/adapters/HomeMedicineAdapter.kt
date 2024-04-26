package com.example.pillstime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.databinding.MedicineCardBinding
import com.example.pillstime.model.DoseTime
import com.example.pillstime.model.Medicine

class HomeMedicineAdapter : RecyclerView.Adapter<HomeMedicineAdapter.MedicineViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        Log.d("HomeMedicineAdapter", "onCreateViewHolder")
        val binding =
            MedicineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class MedicineViewHolder(private val binding: MedicineCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(medItem: Medicine?) {
            val flag = false
            Log.d("HomeMedicineAdapter", "bind at position: $adapterPosition")
            binding.medicineName.text = medItem?.medicineName
            binding.quantity.text = medItem?.amount.toString()
            binding.medicineType.text = medItem?.medicineType


            when (medItem?.medicineType) {
                "Tablet" -> binding.imageView.setImageResource(R.drawable.tablet)
                "Capsule" -> binding.imageView.setImageResource(R.drawable.pill)
                "Liquid" -> binding.imageView.setImageResource(R.drawable.liquid)
                "Injection" -> binding.imageView.setImageResource(R.drawable.syringe)
                "Spray" -> binding.imageView.setImageResource(R.drawable.spray)
            }

            val size = medItem?.doseTimes?.size ?: 0 // Make sure to handle null case

            val list: MutableList<String> = mutableListOf()

            for (i in 0 until size) {
                val item = medItem?.doseTimes!![i]
                val time = "${item.hour}:${item.min}"
                list.add(time)
            }
            val finalTime: String = list.joinToString(" ")
            binding.medicineTime.text = finalTime

            binding.root.setOnClickListener {

                binding.status.visibility =
                    if (binding.status.visibility == View.VISIBLE) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                if (binding.status.visibility == View.VISIBLE) {
                    binding.medicineDetailBtn.setImageResource(R.drawable.up)
                } else {
                    binding.medicineDetailBtn.setImageResource(R.drawable.down)
                }
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
