package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemTaskSelectedAnggotaBinding
import com.example.kelomproapp.models.SelectedAnggota

data class TaskAnggotaListItemsAdapter (private val context: Context,
                                        private var list : ArrayList<SelectedAnggota>,
                                        private var assignedMembers: Boolean) :
    RecyclerView.Adapter<TaskAnggotaListItemsAdapter.TaskAnggotaViewHolder>(){

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAnggotaViewHolder {
        val binding = ItemTaskSelectedAnggotaBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return TaskAnggotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskAnggotaViewHolder, position: Int) {
        val model = list[position]

        if (holder is TaskAnggotaViewHolder){
            if (position == list.size - 1 && assignedMembers ){
                holder.binding.ivAddMember.visibility = View.VISIBLE
                holder.binding.ivSelectedMemberImage.visibility = View.GONE
            }else{
                holder.binding.ivAddMember.visibility = View.GONE
                holder.binding.ivSelectedMemberImage.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.binding.ivSelectedMemberImage)

            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick()
    }

    inner class TaskAnggotaViewHolder(val binding: ItemTaskSelectedAnggotaBinding) :
        RecyclerView.ViewHolder(binding.root)


}
