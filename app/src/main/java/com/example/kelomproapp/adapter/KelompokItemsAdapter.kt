package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemKelompokBinding
import com.example.kelomproapp.models.Kelompok

class KelompokItemsAdapter(private val context: Context,
                           private var list : ArrayList<Kelompok> ) :
    RecyclerView.Adapter<KelompokItemsAdapter.KelompokViewHolder>(){

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelompokViewHolder {
        val binding = ItemKelompokBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return KelompokViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KelompokViewHolder, position: Int) {
        val model = list[position]

        if (holder is KelompokViewHolder){
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



            holder.itemView.setOnClickListener {
                val index = list.indexOf(model) // Dapatkan indeks model
                if (onClickListener != null && index != -1) {
                    onClickListener!!.onClick(index, model) // Gunakan indeks ini untuk memanggil onClickListener
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int,model: Kelompok)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    inner class KelompokViewHolder(val binding: ItemKelompokBinding) :
        RecyclerView.ViewHolder(binding.root)
}