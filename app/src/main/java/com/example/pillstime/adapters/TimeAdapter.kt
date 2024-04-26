package com.example.pillstime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.databinding.TimeCardBinding
import com.example.pillstime.model.DoseTime

class TimeAdapter(private val itemClickListener: OnTimeItemClickListener) :
    RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {


    interface OnTimeItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeAdapter.TimeViewHolder {
        val binding = TimeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeAdapter.TimeViewHolder, position: Int) {
        val timeItem = differ.currentList[position]

        Log.d("onBind-->","${timeItem.toString()} position:${position}" )
        holder.bind(timeItem)

    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class TimeViewHolder(private val binding: TimeCardBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val timeItem = differ.currentList[position]
                itemClickListener.onItemClick(position)
            }
        }

        fun bind(timeItem: DoseTime?) {
            val time = "${timeItem?.hour}:${timeItem?.min} ${timeItem?.am_pm}"
            binding.time.text = time
        }

    }


    private val differCallback = object : DiffUtil.ItemCallback<DoseTime>() {
        override fun areItemsTheSame(oldItem: DoseTime, newItem: DoseTime): Boolean {
            return (oldItem.hour == newItem.hour &&
                    oldItem.min == newItem.min &&
                    oldItem.am_pm == newItem.am_pm)
        }

        override fun areContentsTheSame(oldItem: DoseTime, newItem: DoseTime): Boolean {
            return  oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)


}