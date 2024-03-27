package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemTaskBinding
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Task
import java.util.*
import kotlin.collections.ArrayList

class MyTaskItemsAdapter(
    private val context: Context,
    private val taskList: ArrayList<Task>
) : RecyclerView.Adapter<MyTaskItemsAdapter.TaskViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.binding.tvCardName.text = task.name
        val isCompleted = task.pdfUrl.isNotEmpty()
        val horizontalView = holder.itemView.findViewById<View>(R.id.horizontal_only)
        val tvStatus = holder.itemView.findViewById<TextView>(R.id.tv_status)
        val tvSisaWaktu = holder.itemView.findViewById<TextView>(R.id.tv_sisa_waktu)
//        val kelompok: Kelompok = task.kelompok ?: Kelompok()




        if (isCompleted) {
            horizontalView.setBackgroundColor(ContextCompat.getColor(context, R.color.green_gpt))
            tvStatus.text = "Selesai"
        } else {
            horizontalView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            tvStatus.text = "Belum Selesai"
        }

        if (task.dueDate == 0L){
            tvSisaWaktu.visibility = View.GONE
        }else{
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            dueDate.timeInMillis = task.dueDate //
            val diffInMillis = dueDate.timeInMillis - currentDate.timeInMillis
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
            val diffInHours = (diffInMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)

            tvSisaWaktu.text = "Sisa waktu: $diffInDays hari $diffInHours jam"

            if (diffInMillis < 0) {
                tvSisaWaktu.setTextColor(ContextCompat.getColor(context, R.color.red))
                tvSisaWaktu.text = "Waktu telah lewat"
            } else {
                tvSisaWaktu.text = "Sisa waktu: $diffInDays hari $diffInHours jam"
            }
        }

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position)
        }

        holder.binding.llMyTask.visibility = View.VISIBLE
//        holder.binding.tvKelompok.text = kelompok.name
//        holder.binding.tvMataPelajaran.text = kelompok.course
//        holder.binding.tvTopik.text = kelompok.topic
    }


    override fun getItemCount(): Int {
        return taskList.size
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)
}
