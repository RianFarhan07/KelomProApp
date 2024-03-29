package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.adapter.MateriItemsAdapter
import com.example.kelomproapp.databinding.ActivityMateriBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.utils.Constants

class MateriActivity : BaseActivity() {
    private var binding : ActivityMateriBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMateriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()
        getMateriItemList()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarMateriActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding?.toolbarMateriActivity?.setNavigationOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun getMateriItemList(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().getAllMateriList(this)
    }

    fun populateMateriListToUI(materiList: ArrayList<Materi>){

        val rvMateriList : RecyclerView = findViewById(R.id.rv_materi_list)
        val tvNoMateriAvailable : TextView = findViewById(R.id.tv_no_materi_available)

        hideProgressDialog()

        if (materiList.isNotEmpty()){
            rvMateriList.visibility = View.VISIBLE
            tvNoMateriAvailable.visibility  = View.GONE

            rvMateriList.layoutManager = LinearLayoutManager(this)
            rvMateriList.setHasFixedSize(true)

            val adapter = MateriItemsAdapter(this, materiList)
            rvMateriList.adapter = adapter

            adapter.setOnClickListener(object: MateriItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Materi) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(model.url)
                    startActivity(intent)
                }
            })
        } else {
            rvMateriList.visibility = View.GONE
            tvNoMateriAvailable.visibility  = View.VISIBLE
        }
    }

}