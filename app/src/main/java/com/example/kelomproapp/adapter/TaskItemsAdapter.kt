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
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemTaskBinding
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.SelectedAnggota
import com.example.kelomproapp.models.Task
import com.example.kelomproapp.ui.activities.TaskListActivity
import kotlinx.android.synthetic.main.activity_task_list.view.*

class TaskItemsAdapter (private val context: Context,
                        private var list : ArrayList<Task>, ) :
    RecyclerView.Adapter<TaskItemsAdapter.TaskViewHolder>(){

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

        if (holder is TaskViewHolder){
            holder.binding.tvCardName.text = "${model.name}"

            if (isCompleted) {
                horizontalView.setBackgroundColor(ContextCompat.getColor(context, R.color.green_gpt))
                tvStatus.text = "Selesai"
            } else {
                // Mengubah warna latar belakang horizontal view menjadi warna aslinya jika tugas belum selesai
                horizontalView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                tvStatus.text = "Belum Selesai"
            }

            if ((context as TaskListActivity).mAssignedAnggotaDetailList.size > 0){

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