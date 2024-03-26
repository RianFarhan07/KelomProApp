package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemAnggotaBinding
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Siswa

class GuruItemsAdapter(private val context: Context,
                       private val list : ArrayList<Guru>,)
        : RecyclerView.Adapter<GuruItemsAdapter.GuruViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuruViewHolder {
        val binding = ItemAnggotaBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return GuruViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuruViewHolder, position: Int) {
        val model = list[position]

        if (holder is GuruViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.binding.ivAnggotaImage)

            holder.binding.tvNamaAnggota.text = model.name
            holder.binding.tvEmailAnggota.text = model.email


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class GuruViewHolder(val binding: ItemAnggotaBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(position: Int, siswa: Siswa, action: String)
    }
}