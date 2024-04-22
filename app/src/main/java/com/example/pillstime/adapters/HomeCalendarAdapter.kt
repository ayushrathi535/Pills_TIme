package com.example.pillstime.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.databinding.ItemCalendarCardBinding

import com.example.pillstime.model.CalendarModel
import com.example.pillstime.model.DoseTime
import com.example.pillstime.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeCalendarAdapter(private val listener: OnDateItemClickListener) :
    RecyclerView.Adapter<HomeCalendarAdapter.CalendarAdapter>() {
    private var selectedIndex: Int = RecyclerView.NO_POSITION

    // this remove blinking when clicking items
    // itemAnimator = null
    interface OnDateItemClickListener {
        fun onItemClick(date: Date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter {
        val binding =
            ItemCalendarCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarAdapter(binding)
    }

    fun getCalendarList(list: List<Date>) {

        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: CalendarAdapter,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val date = differ.currentList[position]
        holder.bind(date, position == selectedIndex)

        holder.itemView.setOnClickListener {
            if (position != selectedIndex) {
                selectedIndex = position

                listener.onItemClick(date = date)
                notifyDataSetChanged()
            }

            //below code is also works
//            if (position != selectedIndex) {
//                val previousIndex = selectedIndex
//                selectedIndex = position
//                notifyItemChanged(previousIndex)
//                notifyItemChanged(selectedIndex)
//            }
        }
    }

    inner class CalendarAdapter(private val binding: ItemCalendarCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date, isSelected: Boolean) {
            binding.date.text = DateUtils.getDayNumber(date)
            binding.day.text = DateUtils.getDayName(date).substring(0, 2)

            if (isSelected) {
                binding.circle.setBackgroundResource(R.drawable.calendar_outline_shape)
                binding.date.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )

            } else {
                binding.date.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.medium_blue
                    )
                )
                binding.circle.setBackgroundResource(R.drawable.calendar_shape)
            }

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Date>() {
        override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean {

            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)
}

