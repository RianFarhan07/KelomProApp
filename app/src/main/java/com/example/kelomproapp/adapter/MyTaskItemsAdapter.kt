package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.databinding.ItemTaskBinding
import com.example.kelomproapp.models.Task

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

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position)
        }
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
