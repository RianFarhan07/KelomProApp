package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ItemMateriBinding
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi

class MateriItemsAdapter (private val context: Context,
                          private var list : ArrayList<Materi> ) :
    RecyclerView.Adapter<MateriItemsAdapter.MateriViewHolder>(){

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val binding = ItemMateriBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return MateriViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        val model = list[position]

        if (holder is MateriViewHolder){

            holder.binding.tvNamaMateri.text = model.name
            holder.binding.textViewTopik.text = "Topik = ${model.topic}"
            holder.binding.textViewMataPelajaran.text = "Mata Pelajaran = ${model.courses}"

            when (model.fileType) {
                "pdf" -> holder.binding.imageViewPdfLogo.setImageResource(R.drawable.pdf)
                "doc", "docx" -> holder.binding.imageViewPdfLogo.setImageResource(R.drawable.word)
                "ppt", "pptx" -> holder.binding.imageViewPdfLogo.setImageResource(R.drawable.ppt)
                else -> holder.binding.imageViewPdfLogo.setImageResource(R.drawable.pdf)
            }

            holder.itemView.setOnClickListener {
                val index = list.indexOf(model) // Dapatkan indeks model
                if (onClickListener != null && index != -1) {
                    onClickListener!!.onClick(index, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int,model: Materi)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    inner class MateriViewHolder(val binding: ItemMateriBinding) :
        RecyclerView.ViewHolder(binding.root)
}