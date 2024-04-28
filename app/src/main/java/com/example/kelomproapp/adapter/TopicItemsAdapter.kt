package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemCourseBinding
import com.example.kelomproapp.databinding.ItemTopicBinding
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Topic

class TopicItemsAdapter (private val context: Context,
                         private val list : ArrayList<Topic>,)
    : RecyclerView.Adapter<TopicItemsAdapter.TopicViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val model = list[position]

        if (holder is TopicViewHolder){
            holder.binding.textViewTopicName.text = model.name
            holder.binding.ivCourseItemImage.setImageResource(R.drawable.course)

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

    class TopicViewHolder(val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(position: Int,model: Topic)
    }
}