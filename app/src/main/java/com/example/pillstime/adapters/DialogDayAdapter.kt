package com.example.pillstime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.databinding.DayItemCardBinding
import com.example.pillstime.model.Days
import com.example.pillstime.model.Weeks

class DialogDayAdapter(private val list: List<Days>) : RecyclerView.Adapter<DialogDayAdapter.ViewHolder>() {

    private val selectedDays = mutableSetOf<Int>()
    private var isEveryDaySelected = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DayItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = list[position]
        holder.bind(day)
        holder.itemView.setOnClickListener {
            toggleSelection(position)
        }
        holder.bindUI(selectedDays.contains(position))
    }
    fun getSelectedDays(): List<Days> {
        return selectedDays.map { list[it] }
    }
     private fun toggleSelection(position: Int) {
        if (selectedDays.contains(position)) {
            selectedDays.remove(position)
        } else {
            selectedDays.add(position)
        }
        notifyDataSetChanged()
    }

    fun everyDay() {
        if (isEveryDaySelected) {
            selectedDays.clear()
        } else {
            selectedDays.addAll(list.indices)
        }
        isEveryDaySelected = !isEveryDaySelected
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: DayItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(days: Days) {
            binding.apply {
                date.text = days.day
            }
        }

        fun bindUI(isSelected: Boolean) {
            binding.apply {
                val backgroundDrawable = if (isSelected) R.drawable.calendar_shape_green else R.drawable.calendar_shape
                layout.setBackgroundResource(backgroundDrawable)
                val textColor = if (isSelected) R.color.white else R.color.medium_blue
                date.setTextColor(itemView.context.getColor(textColor))
            }
        }
    }
}
