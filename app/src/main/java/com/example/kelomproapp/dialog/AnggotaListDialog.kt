package com.example.kelomproapp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelomproapp.adapter.SiswaItemsAdapter
import com.example.kelomproapp.databinding.DialogListBinding
import com.example.kelomproapp.models.Siswa

abstract class AnggotaListDialog (
    context : Context,
    private val list : ArrayList<Siswa> = ArrayList(),
    private val title : String = ""
) : Dialog(context){
    private var adapter : SiswaItemsAdapter? = null
    private var binding : DialogListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogListBinding.inflate(LayoutInflater.from(context))

        val view = binding?.root

        setContentView(view!!)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View){
        binding?.tvTitle?.text = title

        if (list.size > 0){
            binding?.rvList?.layoutManager = LinearLayoutManager(context)
            adapter = SiswaItemsAdapter(context,list)
            binding?.rvList?.adapter = adapter

            adapter!!.setOnClickListener  (object : SiswaItemsAdapter.OnClickListener{
                override fun onClick(position: Int, siswa: Siswa, action: String) {
                    dismiss()
                    onItemSelected(siswa,action)
                }
            })
        }

    }
    protected abstract fun onItemSelected(siswa: Siswa, action : String)



}