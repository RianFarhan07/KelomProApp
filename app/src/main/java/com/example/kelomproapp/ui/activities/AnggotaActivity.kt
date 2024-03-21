package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.AnggotaItemsAdapter
import com.example.kelomproapp.databinding.ActivityAnggotaBinding
import com.example.kelomproapp.databinding.DialogSearchAnggotaBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants

class AnggotaActivity : BaseActivity() {
    private var binding : ActivityAnggotaBinding? = null
    private lateinit var mKelompokDetails : Kelompok
    private lateinit var mAssignedAnggotaList : ArrayList<Siswa>
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnggotaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.KELOMPOK_DETAIL)){
            mKelompokDetails = intent.getParcelableExtra<Kelompok>(Constants.KELOMPOK_DETAIL)!!
        }

        setupActionBar()
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        FirestoreClass().getAssignedAnggotaListDetails(this,mKelompokDetails.assignedTo)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarAnggotaActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarAnggotaActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_anggota,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_anggota -> {
                dialogSearchAnggota()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun setupAnggotaList(list: ArrayList<Siswa>){
        hideProgressDialog()
        mAssignedAnggotaList = list

        binding?.rvAnggotaList?.layoutManager = LinearLayoutManager(this)
        binding?.rvAnggotaList?.setHasFixedSize(true)

        val adapter = AnggotaItemsAdapter(this,list)

        binding?.rvAnggotaList?.adapter = adapter
    }

    fun anggotaDetails(siswa: Siswa){
        mKelompokDetails.assignedTo.add(siswa.id)
        FirestoreClass().assignedAnggotaToKelompok(this,mKelompokDetails,siswa)
    }

    fun anggotaAssignedSuccess(siswa: Siswa){
        hideProgressDialog()
        mAssignedAnggotaList.add(siswa)
        setupAnggotaList(mAssignedAnggotaList)
        anyChangesMade = true

//        SendNotificationToUserAsyncTask(mBoardDetails.name!!,user.fcmToken!!).execute()
    }

    private fun dialogSearchAnggota() {
        val binding = DialogSearchAnggotaBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)

        binding.tvAdd.setOnClickListener {
            val email = binding.etEmailSearchAnggota.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.mohon_tunggu))
                FirestoreClass().getAnggotaDetails(this,email)

            } else {
                Toast.makeText(this@AnggotaActivity, "Harap Masukkan Email Anggota",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}