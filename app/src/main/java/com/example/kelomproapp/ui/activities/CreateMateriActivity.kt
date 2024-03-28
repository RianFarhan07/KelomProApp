package com.example.kelomproapp.ui.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityCreateMateriBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateMateriActivity : BaseActivity() {
    private var binding : ActivityCreateMateriBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateMateriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCreateMateriActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarCreateMateriActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun createMateri(){

        var materi = Materi(
            binding?.etMateriName?.text.toString(),
            binding?.etCourse?.text.toString(),
            binding?.etTopic?.text.toString(),
//            assignedUserArrayList,
        )

//        FirestoreClass().createKelompok(this,kelompok)
    }

    fun kelompokCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }


}