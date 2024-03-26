package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemAnggotaBinding
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants

class SiswaItemsAdapter(private val context: Context,
                        private val list : ArrayList<Siswa>,)
        : RecyclerView.Adapter<SiswaItemsAdapter.AnggotaViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnggotaViewHolder {
        val binding = ItemAnggotaBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return AnggotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnggotaViewHolder, position: Int) {
        val model = list[position]

        if (holder is AnggotaViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.binding.ivAnggotaImage)

            holder.binding.tvNamaAnggota.text = "${model.firstName} ${model.lastName}"
            holder.binding.tvEmailAnggota.text = model.email

            if (model.selected){
                holder.binding.ivSelectedMember.visibility = View.VISIBLE
            }else{
                holder.binding.ivSelectedMember.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    if (model.selected){
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class AnggotaViewHolder(val binding: ItemAnggotaBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(position: Int, siswa: Siswa, action: String)
    }
}