package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemAnggotaBinding
import com.example.kelomproapp.databinding.ItemCourseBinding
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa

class CourseItemsAdapter (private val context: Context,
                          private val list : ArrayList<Course>,)
    : RecyclerView.Adapter<CourseItemsAdapter.CourseViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val model = list[position]

        if (holder is CourseViewHolder){
            holder.binding.textViewCourseName.text = model.name
            holder.binding.textViewGuru.text = "Guru ${model.guru}"
            holder.binding.textViewClasses.text = "Kelas ${model.classes}"

        }

        holder.binding.root.setOnClickListener {
            onClickListener?.onClick(position, model)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class CourseViewHolder(val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(position: Int,model: Course)
    }
}