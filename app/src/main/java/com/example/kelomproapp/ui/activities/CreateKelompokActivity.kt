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
import com.example.kelomproapp.databinding.ActivityCreateKelompokBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Topic
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateKelompokActivity : BaseActivity() {

    private var binding : ActivityCreateKelompokBinding? = null
    private lateinit var mUsername : String
    private var mToCourse : Boolean = false
    private var mSelectedImageFileUri : Uri? = null
    private var mKelompokImageURL : String = ""
    private var mTopicListPosition = -1
    private lateinit var mCourseDetail : Course


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateKelompokBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)


        setupActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUsername = intent.getStringExtra(Constants.NAME).toString()
            Log.e("NAMA GURU ",mUsername)
        }
        if (intent.hasExtra(Constants.TO_COURSE)){
            mToCourse = intent.getBooleanExtra(Constants.TO_COURSE,false)
            Log.e("TOCOURSE ", mToCourse.toString())
        }
        if (intent.hasExtra(Constants.TOPIC_LIST_ITEM_POSITION)){
            mTopicListPosition = intent.getIntExtra(Constants.TOPIC_LIST_ITEM_POSITION,-1)
            Log.e("mtopiclistposition ", mTopicListPosition.toString())
        }
        if (intent.hasExtra(Constants.COURSE_DETAIL)){
            mCourseDetail = intent.getParcelableExtra(Constants.COURSE_DETAIL)!!
        }


        binding?.ivKelompokImage?.setOnClickListener {
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

        binding?.btnCreate?.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadKelompokImage()
            }else{
                showProgressDialog(resources.getString(R.string.mohon_tunggu))
                if (mToCourse){
                    createKelompokInTopicList()
                }else{
                    createKelompok()
                }
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarCreateKelompokActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding?.toolbarCreateKelompokActivity?.setNavigationOnClickListener { onBackPressed() }
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
                    .into(findViewById(R.id.iv_kelompok_image))
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun createKelompok() {
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(FirestoreClass().getCurrentUserID())

        val kelompok = Kelompok(
            binding?.etKelompokName?.text.toString(),
            mKelompokImageURL,
            mUsername,
            assignedUserArrayList,
            binding?.etCourse?.text.toString(),
            binding?.etClasses?.text.toString(),
            binding?.etTopic?.text.toString()
        )

        val courseName = binding?.etCourse?.text.toString()

//        FirestoreClass().getCourseList { courseList ->
//            var existingCourse: Course? = null
//
//            for (course in courseList) {
//                if (course.name == courseName) {
//                    existingCourse = course
//                    break
//                }
//            }
//
//            if (existingCourse != null) {
//                existingCourse.kelompok?.add(kelompok)
//                FirestoreClass().updateCourse(this, existingCourse)
//            } else {
//                val assignedKelompokArrayList: ArrayList<Kelompok> = ArrayList()
//                assignedKelompokArrayList.add(kelompok)
//
//                val newClass = Course(
//                    courseName,
//                    assignedKelompokArrayList
//                )
//
//                FirestoreClass().createCourse(this,newClass)
//            }

            FirestoreClass().createKelompok(this,kelompok)
//        }
    }


    fun kelompokCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
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

                createKelompok()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(this@CreateKelompokActivity,
                exception.message,
                Toast.LENGTH_LONG
            ).show()
            hideProgressDialog()
        }
    }

    fun CourseDetails(course: Course){
        mCourseDetail = course
    }

    fun createKelompokInTopicList(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(FirestoreClass().getCurrentUserID())

        val kelompok = Kelompok(
            binding?.etKelompokName?.text.toString(),
            mKelompokImageURL,
            mUsername,
            assignedUserArrayList,
            binding?.etCourse?.text.toString(),
            binding?.etClasses?.text.toString(),
            binding?.etTopic?.text.toString()
        )
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        mCourseDetail.topicList[mTopicListPosition].kelompok.add(0,kelompok)
        FirestoreClass().addUpdateTopicList(this, mCourseDetail)
    }






}