package com.example.pillstime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pillstime.R
import com.example.pillstime.databinding.MedicineCardBinding
import com.example.pillstime.model.DoseTime
import com.example.pillstime.model.Medicine
import com.example.pillstime.model.MedicineTrack
import com.example.pillstime.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeMedicineAdapter(
    private var list: MutableList<MedicineTrack>? = null,
    private val listener: OnTakenListener
) : RecyclerView.Adapter<HomeMedicineAdapter.MedicineViewHolder>() {

    private var date: Date? = null

    interface OnTakenListener {
        fun onTakenClick(medId: Long)

    }
fun getUpdatedList(list: MutableList<MedicineTrack>){
    this.list=list
    notifyDataSetChanged()
}
    fun getDate(date: Date) {
        this.date = date
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
       // Log.d("HomeMedicineAdapter", "onCreateViewHolder")
        val binding =
            MedicineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //Log.d("HomeMedicineAdapter", "getItemCount: ${differ.currentList.size}")
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
      //  Log.d("HomeMedicineAdapter", "onBindViewHolder at position: $position")
        val medItem = differ.currentList[position]
        holder.bind(medItem)
    }

    inner class MedicineViewHolder(private val binding: MedicineCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(medItem: Medicine?) {
            val medId = medItem?.id
            val date = this@HomeMedicineAdapter.date
            Log.e("adapter date-->", date.toString())
            Log.e("adapter medID-->", medId.toString())

            Log.e("adapter list-->",this@HomeMedicineAdapter.list.toString())

            if (list != null && medId != null && date != null) {


                Log.e("inside---> ","adapter check pass")
//                val isMedicineTaken = this@HomeMedicineAdapter.list!!.any { it.medId == medId && date in it.date }
                val isMedicineTaken = this@HomeMedicineAdapter.list!!.any { medTrack ->
                    medTrack.medId == medId &&
                            medTrack.date.any { medDate ->
                                Log.e("adapter medDate-->",medDate.toString())
                                Log.e("adapter selDate-->",date.toString())

                                val trackDate= "${DateUtils.getDayNumber(medDate)}${DateUtils.getMonthName(medDate)}"

                                val currentDate= "${DateUtils.getDayNumber(date)}${DateUtils.getMonthName(date)}"

                                Log.e("adapter medDate-->",trackDate.toString())
                                Log.e("adapter selDate-->",currentDate.toString())

                                trackDate==currentDate

                            }
                }

                if (isMedicineTaken) {
                    Log.e("inside---> ","adapter check pass 2")
                    // Medicine has been taken on the specified date
                    binding.imageView.setBackgroundResource(R.drawable.calendar_shape_green)
                    binding.imageView.setImageResource(R.drawable.tick)
                    binding.medCard.isEnabled=false
                } else {
                    when (medItem?.medicineType) {

                        "Tablet" -> binding.imageView.setImageResource(R.drawable.tablet)
                        "Capsule" -> binding.imageView.setImageResource(R.drawable.pill)
                        "Liquid" -> binding.imageView.setImageResource(R.drawable.liquid)
                        "Injection" -> binding.imageView.setImageResource(R.drawable.syringe)
                        "Spray" -> binding.imageView.setImageResource(R.drawable.spray)
                    }
                    binding.medCard.isEnabled=true
                    binding.imageView.setBackgroundResource(R.drawable.calendar_shape)
                }
            }


            val flag = false
//            Log.d("HomeMedicineAdapter", "bind at position: $adapterPosition")
            binding.medicineName.text = medItem?.medicineName
            binding.quantity.text = medItem?.amount.toString()
            binding.medicineType.text = medItem?.medicineType


//            when (medItem?.medicineType) {
//                "Tablet" -> binding.imageView.setImageResource(R.drawable.tablet)
//                "Capsule" -> binding.imageView.setImageResource(R.drawable.pill)
//                "Liquid" -> binding.imageView.setImageResource(R.drawable.liquid)
//                "Injection" -> binding.imageView.setImageResource(R.drawable.syringe)
//                "Spray" -> binding.imageView.setImageResource(R.drawable.spray)
//            }

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

            binding.take.setOnClickListener {
                binding.imageView.setBackgroundResource(R.drawable.calendar_shape_green)
                binding.imageView.setImageResource(R.drawable.tick)
                binding.status.visibility=View.GONE
                binding.medicineDetailBtn.setImageResource(R.drawable.down)
                listener.onTakenClick(medItem!!.id)
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
