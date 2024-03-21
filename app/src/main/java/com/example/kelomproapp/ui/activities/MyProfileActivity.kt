package com.example.kelomproapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityMyProfileBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var binding : ActivityMyProfileBinding? = null
    private lateinit var mSiswaDetails : Siswa
    private var mSelectedImageFileUri :Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()

        FirestoreClass().getUserDetails(this)

        binding?.ivProfileUserImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnSubmit?.setOnClickListener{
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.mohon_tunggu))

                updateUserProfileData()
            }
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarUpdateProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding?.toolbarUpdateProfileActivity?.setNavigationOnClickListener { onBackPressed() }
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
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(findViewById(R.id.iv_profile_user_image))
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        if (mSelectedImageFileUri != null){

            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE" + System.currentTimeMillis()
                            + "." + Constants.getFileExtension(this,mSelectedImageFileUri!!))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapShot ->
                Log.e(
                    "Firebase image URL",
                    taskSnapShot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e(
                        "Downloadable Image URL",
                        uri.toString())
                    mUserProfileImageURL = uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }

        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if (mUserProfileImageURL.isNotEmpty() && mUserProfileImageURL != mSiswaDetails.image){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
            anyChangesMade = true
        }

        if (binding?.etFirstName?.text.toString() != mSiswaDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = binding?.etFirstName?.text.toString()
            anyChangesMade = true
        }
        if (binding?.etLastName?.text.toString() != mSiswaDetails.lastName){
            userHashMap[Constants.LAST_NAME] = binding?.etLastName?.text.toString()
            anyChangesMade = true
        }
        if (binding?.etMobile?.text.toString() != mSiswaDetails.mobile){
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString()
            anyChangesMade = true
        }
        if (binding?.etClasses?.text.toString() != mSiswaDetails.classes){
            userHashMap[Constants.CLASSES] = binding?.etClasses?.text.toString()
            anyChangesMade = true
        }

        if (anyChangesMade){
            FirestoreClass().updateUserProfileData(this,userHashMap)
        }else{
            Toast.makeText(this,"there are no changes made",Toast.LENGTH_SHORT).show()
            hideProgressDialog()
        }

    }

    fun setUserDataInUI(siswa: Siswa){
        mSiswaDetails = siswa

        Glide
            .with(this)
            .load(siswa.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.iv_profile_user_image))

        binding?.etFirstName?.setText(siswa.firstName)
        binding?.etLastName?.setText(siswa.lastName)
        binding?.etEmail?.setText(siswa.email)
        binding?.etClasses?.setText(siswa.classes)
        binding?.etMobile?.setText(siswa.mobile)

    }

    fun profileUpdateSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }
}