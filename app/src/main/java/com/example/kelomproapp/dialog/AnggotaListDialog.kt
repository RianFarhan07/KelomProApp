package com.example.kelomproapp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelomproapp.adapter.AnggotaItemsAdapter
import com.example.kelomproapp.databinding.DialogListBinding
import com.example.kelomproapp.models.User

abstract class AnggotaListDialog (
    context : Context,
    private val list : ArrayList<User> = ArrayList(),
    private val title : String = ""
) : Dialog(context){
    private var adapter : AnggotaItemsAdapter? = null
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
            adapter = AnggotaItemsAdapter(context,list)
            binding?.rvList?.adapter = adapter

            adapter!!.setOnClickListener  (object : AnggotaItemsAdapter.OnClickListener{
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user,action)
                }
            })
        }

    }
    protected abstract fun onItemSelected(user: User, action : String)



}