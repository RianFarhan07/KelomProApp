package com.example.kelomproapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemTaskBinding
import com.example.kelomproapp.models.SelectedAnggota
import com.example.kelomproapp.models.Task
import com.example.kelomproapp.ui.activities.CourseTaskActivity
import com.example.kelomproapp.ui.activities.TaskListActivity
import java.util.*
import kotlin.collections.ArrayList

class TaskInCourseAdapter (private val context: Context,
                           private var list : ArrayList<Task>, ) :
    RecyclerView.Adapter<TaskInCourseAdapter.TaskViewHolder>(){

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]
        val isCompleted = model.pdfUrl.isNotEmpty()
        val horizontalView = holder.itemView.findViewById<View>(R.id.horizontal_only)
        val tvStatus = holder.itemView.findViewById<TextView>(R.id.tv_status)
        val tvSisaWaktu = holder.itemView.findViewById<TextView>(R.id.tv_sisa_waktu)

        if (holder is TaskViewHolder){
            holder.binding.tvCardName.text = "${model.name}"

            if (isCompleted) {
                horizontalView.setBackgroundColor(ContextCompat.getColor(context, R.color.green_gpt))
                tvStatus.text = "Selesai"
            } else {
                horizontalView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                tvStatus.text = "Belum Selesai"
            }

            if (model.dueDate == 0L){
                tvSisaWaktu.visibility = View.GONE
            }else{
                val currentDate = Calendar.getInstance()
                val dueDate = Calendar.getInstance()
                dueDate.timeInMillis = model.dueDate //
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

            if ((context as CourseTaskActivity).mAssignedAnggotaDetailList.size > 0){

                val selectedAnggotaList: ArrayList<SelectedAnggota> = ArrayList()

                for (i in context.mAssignedAnggotaDetailList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedAnggotaDetailList[i].id==j){
                            val selectedMembers = SelectedAnggota(
                                context.mAssignedAnggotaDetailList[i].id!!,
                                context.mAssignedAnggotaDetailList[i].image!!
                            )
                            selectedAnggotaList.add(selectedMembers)
                        }
                    }
                }
                if(selectedAnggotaList.size >0){
                    if (selectedAnggotaList.size == 1 && selectedAnggotaList[0].id == model.createdBy){
                        holder.binding.rvCardSelectedMembersList.visibility = View.GONE
                    }else{
                        holder.binding.rvCardSelectedMembersList.visibility = View.VISIBLE

                        holder.binding.rvCardSelectedMembersList.layoutManager =
                            GridLayoutManager(context,4)
                        val adapter = TaskAnggotaListItemsAdapter(context,selectedAnggotaList,false)

                        holder.binding.rvCardSelectedMembersList.adapter = adapter

                        adapter.setOnClickListener(object : TaskAnggotaListItemsAdapter.OnClickListener{
                            override fun onClick() {
                                if (onClickListener != null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }else{
                    holder.binding.rvCardSelectedMembersList.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)
}