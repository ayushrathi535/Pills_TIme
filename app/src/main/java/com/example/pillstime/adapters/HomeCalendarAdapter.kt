package com.example.pillstime.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.databinding.ItemCalendarCardBinding
import com.example.pillstime.model.CalendarDateModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CalendarAdapter(private val listener: (calendarDateModel: CalendarDateModel, position: Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    private val list = ArrayList<CalendarDateModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCalendarCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val modal= list[position]
        val result= areDatesEqual(modal.data.toString())
        Log.e("adapter-->","${result}")
        if (result){
            holder.bind(modal,true)
        }
        holder.bind(modal)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }


    inner class MyViewHolder(private val binding: ItemCalendarCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SuspiciousIndentation")
        fun bind(calendarDateModel: CalendarDateModel,flag:Boolean ?=false) {

            if (calendarDateModel.isSelected) {
                binding.date.text = calendarDateModel.calendarDate
                binding.day.text = calendarDateModel.calendarDay

                binding.dateContainer.setBackgroundResource(R.drawable.calendar_outline_shape)
                binding.date.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )

            } else {
                binding.date.text = calendarDateModel.calendarDate
                binding.day.text = calendarDateModel.calendarDay
                binding.date.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.medium_blue
                    )
                )
                binding.dateContainer.setBackgroundResource(R.drawable.calendar_shape)
            }


//            if(flag == true){
//                listener.invoke(calendarDateModel,adapterPosition)
//            }
            Log.e("adapter position-->",adapterPosition.toString())
            binding.dateContainer.setOnClickListener {
                listener.invoke(calendarDateModel, adapterPosition)
            }
        }

    }

    fun areDatesEqual(date1: String): Boolean {

         val cal = Calendar.getInstance(Locale.ENGLISH)
        val currentDate = Calendar.getInstance(Locale.ENGLISH)

        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val cal1 = Calendar.getInstance().apply { time = sdf.parse(date1)!! }
        val cal2 = Calendar.getInstance().apply { time = sdf.parse(currentDate.time.toString())!! }

        // Compare day, month, and year
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}
