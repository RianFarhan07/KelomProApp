package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemKelompokBinding
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Topic

class KelompokInCourseAdapter (private val context: Context,
                               private val list : ArrayList<Kelompok>,)
    : RecyclerView.Adapter<KelompokInCourseAdapter.KelompokInCourseViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelompokInCourseViewHolder {
        val binding = ItemKelompokBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return KelompokInCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KelompokInCourseViewHolder, position: Int) {
        val model = list[position]

        if (holder is KelompokInCourseAdapter.KelompokInCourseViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.binding.ivKelompokImage)

            holder.binding.tvName.text = "${model.name} ${model.course}"
            holder.binding.tvCreatedBy.text = "Ketua: ${model.createdBy}"
            holder.binding.tvClasses.text = model.classes
            holder.binding.tvTopic.text = model.topic

            holder.binding.root.setOnClickListener {
                onClickListener?.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: KelompokInCourseAdapter.OnClickListener){
        this.onClickListener = onClickListener
    }

    inner class KelompokInCourseViewHolder(val binding: ItemKelompokBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(position: Int)
    }
}