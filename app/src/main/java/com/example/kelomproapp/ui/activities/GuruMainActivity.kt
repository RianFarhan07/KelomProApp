package com.example.kelomproapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.databinding.ActivityGuruMainBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class GuruMainActivity : BaseActivity() {

    private var binding : ActivityGuruMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGuruMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()

//        binding?.navViewGuru?.setNavigationItemSelectedListener(this)

        FirestoreClass().getUserDetails(this,"guru",true)

    }

    private fun setupActionBar() {
        val toolbar : Toolbar = findViewById(R.id.toolbar_main_activity)

        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener {
            toogleDrawer()
        }

    }

    private fun toogleDrawer(){

        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {

        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)

        } else{
            doubleBackToExit()
        }
    }


    fun updateNavigationGuruDetails(guru : Guru, readKelompokList: Boolean){

        Glide
            .with(this)
            .load(guru.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_user_image))

        var tvUserName : TextView = findViewById(R.id.tv_username)
        tvUserName.text = guru.Name

        if (readKelompokList){
            showProgressDialog(resources.getString(R.string.mohon_tunggu))

            FirestoreClass().getKelompokListGuru(this)
        }
    }

    fun populateKelompokListToUI(kelompokList: ArrayList<Kelompok>){

        val rvKelompokList : RecyclerView = findViewById(R.id.rv_kelompok_list)
        val tvNoKelompokAvailable : TextView = findViewById(R.id.tv_no_kelompok_available)

        hideProgressDialog()

        if (kelompokList.size >0){
            rvKelompokList.visibility = View.VISIBLE
            tvNoKelompokAvailable.visibility  = View.GONE

            rvKelompokList.layoutManager = LinearLayoutManager(this)
            rvKelompokList.setHasFixedSize(true)

            val adapter = KelompokItemsAdapter(this,kelompokList)
            rvKelompokList.adapter = adapter

            adapter.setOnClickListener(object: KelompokItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Kelompok) {
                    val intent = Intent(this@GuruMainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivityForResult(intent, MainActivity.UPDATE_KELOMPOK_REQUEST_CODE)
                }
            })
        }else{
            rvKelompokList.visibility = View.GONE
            tvNoKelompokAvailable.visibility  = View.VISIBLE
        }
    }
}