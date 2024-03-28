package com.example.kelomproapp.ui.activities

import android.content.Intent
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
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.utils.Constants

class MateriActivity : BaseActivity() {
    private var binding : ActivityMateriBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMateriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
    }

    private fun setupActionBar() {
        val toolbar : Toolbar = findViewById(R.id.toolbar_materi_activity)

        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)


    }

    fun populateMateriListToUI(materiList: ArrayList<Materi>){

        val rvMateriList : RecyclerView = findViewById(R.id.rv_materi_list)
        val tvNoKelompokAvailable : TextView = findViewById(R.id.tv_no_materi_available)

        hideProgressDialog()

        if (rvMateriList.size >0){
            rvMateriList.visibility = View.VISIBLE
            tvNoKelompokAvailable.visibility  = View.GONE

            rvMateriList.layoutManager = LinearLayoutManager(this)
            rvMateriList.setHasFixedSize(true)

            val adapter = MateriItemsAdapter(this,materiList)
            rvMateriList.adapter = adapter

//            adapter.setOnClickListener(object: KelompokItemsAdapter.OnClickListener{
//                override fun onClick(position: Int, model: Kelompok) {
//                    val intent = Intent(this@GuruMainActivity, TaskListActivity::class.java)
//                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
//                    startActivityForResult(intent, MainActivity.UPDATE_KELOMPOK_REQUEST_CODE)
//                }
//            })
        }else{
            rvMateriList.visibility = View.GONE
            tvNoKelompokAvailable.visibility  = View.VISIBLE
        }
    }
}