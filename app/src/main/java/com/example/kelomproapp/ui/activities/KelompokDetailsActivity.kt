package com.example.kelomproapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityKelompokDetailsBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class KelompokDetailsActivity : BaseActivity() {

    private var binding : ActivityKelompokDetailsBinding? = null
    private lateinit var mKelompokDetails : Kelompok
    private lateinit var mKelompokDocumentId : String
    private var mSelectedImageFileUri : Uri? = null
    private var mKelompokImageURL : String = ""

    companion object {
        const val ANGGOTA_REQUEST_CODE : Int = 14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityKelompokDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mKelompokDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        FirestoreClass().getKelompokDetails(this,mKelompokDocumentId)
        FirestoreClass().getUserDetails(this,Constants.SISWA)

        setupActionBar()

        binding?.ivProfileKelompokImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnUpdate?.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadKelompokImage()
            }else{
                showProgressDialog(resources.getString(R.string.mohon_tunggu))

                updateKelompokProfileData()
            }
        }

        binding?.btnDelete?.setOnClickListener {
            showAlertDialogToDeleteKelompok(mKelompokDocumentId)
        }

        binding?.tvSelectMembers?.setOnClickListener {
            val intent = Intent(this,AnggotaActivity::class.java)
            intent.putExtra(Constants.KELOMPOK_DETAIL,mKelompokDetails)
            startActivityForResult(intent, ANGGOTA_REQUEST_CODE)
        }

    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarDetailsKelompokActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarDetailsKelompokActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(this,"kamu menolak izin storage, aktifkan di setting", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!= null){
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding?.ivProfileKelompokImage!!)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        if (resultCode == Activity.RESULT_OK
            && requestCode == ANGGOTA_REQUEST_CODE){
            FirestoreClass().getKelompokDetails(this,mKelompokDocumentId)
            FirestoreClass().getUserDetails(this,Constants.SISWA)
        }
    }

    fun setKelompokDataInUI(kelompok: Kelompok) {
        mKelompokDetails = kelompok

        Glide.with(this)
            .load(kelompok.image)
            .centerCrop()
            .placeholder(R.drawable.kelompok_placeholder)
            .into(binding?.ivProfileKelompokImage!!)

        binding?.etNameDetails?.setText(kelompok.name)
        binding?.etCourseDetails?.setText(kelompok.course)
        binding?.etClassesDetails?.setText(kelompok.classes)
        binding?.tvKetuaName?.text = kelompok.createdBy

    }

    fun setUserDataInUI(siswa: Siswa) {
        Glide.with(this)
            .load(siswa.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.ivKetuaImage!!)
    }

    private fun showAlertDialogToDeleteKelompok(kelompokId: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("DELETE")
        builder.setMessage("Apakah anda yakin ingin menghapus kelompok")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Iya") { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.mohon_tunggu))
            FirestoreClass().deleteKelompok(this@KelompokDetailsActivity, kelompokId)
//            FirestoreClass().deleteKelompokFromCourse(this@KelompokDetailsActivity, ,kelompokId)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Tidak") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun kelompokDeleteSuccess(){
        hideProgressDialog()
        val currentUserID = FirestoreClass().getCurrentUserID()
        FirestoreClass().getUserRole(currentUserID) { role ->
            if (role == "guru") {
                startActivity(Intent(this, MainGuruActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    private fun uploadKelompokImage(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        val sRef : StorageReference =
            FirebaseStorage.getInstance().reference.child(
                "KELOMPOK_IMAGE" + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this,mSelectedImageFileUri!!))

        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapShot ->
            Log.e(
                "Firebase Kelompok image URL",
                taskSnapShot.metadata!!.reference!!.downloadUrl.toString()
            )

            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                Log.e(
                    "Downloadable Image URL",
                    uri.toString())
                mKelompokImageURL = uri.toString()

                updateKelompokProfileData()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(this@KelompokDetailsActivity,
                exception.message,
                Toast.LENGTH_LONG
            ).show()
            hideProgressDialog()
        }
    }

    private fun updateKelompokProfileData(){
        val kelompokHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if (mKelompokImageURL.isNotEmpty() && mKelompokImageURL != mKelompokDetails.image){
            kelompokHashMap[Constants.KELOMPOK_IMAGE] = mKelompokImageURL
            anyChangesMade = true
        }

        if (binding?.etNameDetails?.text.toString() != mKelompokDetails.name){
            kelompokHashMap[Constants.KELOMPOK_NAME] = binding?.etNameDetails?.text.toString()
            anyChangesMade = true
        }
        if (binding?.etClassesDetails?.text.toString() != mKelompokDetails.classes){
            kelompokHashMap[Constants.KELOMPOK_CLASSES] = binding?.etClassesDetails?.text.toString()
            anyChangesMade = true
        }
        if (binding?.etCourseDetails?.text.toString() != mKelompokDetails.course){
            kelompokHashMap[Constants.KELOMPOK_COURSES] = binding?.etCourseDetails?.text.toString()
            anyChangesMade = true
        }
//        if (binding?.etClasses?.text.toString() != mUserDetails.classes){
//            userHashMap[Constants.CLASSES] = binding?.etClasses?.text.toString()
//            anyChangesMade = true
//        }

        if (anyChangesMade){
            FirestoreClass().updateKelompokData(this,kelompokHashMap,mKelompokDocumentId)

        }else{
            Toast.makeText(this,"there are no changes made",Toast.LENGTH_SHORT).show()
            hideProgressDialog()
        }

    }
    fun kelompokUpdateSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        startActivity(Intent(this,MainActivity::class.java))
    }
}