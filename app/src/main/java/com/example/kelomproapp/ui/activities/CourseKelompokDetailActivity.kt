package com.example.kelomproapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract.Root
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityCourseKelompokDetailBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.models.Topic
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CourseKelompokDetailActivity : BaseActivity() {

    private var binding : ActivityCourseKelompokDetailBinding? = null
    private lateinit var mCourseDetails : Course
    private lateinit var mCourseDocumentId : String
    private var mSelectedImageFileUri : Uri? = null
    private var mKelompokImageURL : String = ""
    private var mGuruName : String? = null
    private lateinit var mCourseDetail: Course
    private lateinit var mTopicDetail: Topic
    private var mTopicListPosition = -1

    private var mKelompokListPosition = -1
    lateinit var mAssignedAnggotaDetailList: ArrayList<Siswa>



    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseKelompokDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()

        Glide.with(this)
            .load(mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].image)
            .centerCrop()
            .placeholder(R.drawable.kelompok_placeholder)
            .into(binding?.ivProfileKelompokImage!!)

        binding?.etNameDetails?.setText(mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].name)
        binding?.etCourseDetails?.setText(mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].course)
        binding?.etClassesDetails?.setText(mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].classes)
        binding?.tvKetuaName?.text = mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].createdBy

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

                createKelompokInTopicList()
            }
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.COURSE_DETAIL)) {
            mCourseDetail = intent.getParcelableExtra(Constants.COURSE_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TOPIC_LIST_ITEM_POSITION)) {
            mTopicListPosition = intent.getIntExtra(Constants.TOPIC_LIST_ITEM_POSITION, -1)
            Log.e("TOPIC_ITEM_POSITION", mTopicListPosition.toString())
        }
        if (intent.hasExtra(Constants.KELOMPOK_LIST_ITEM_POSITION)){
            mKelompokListPosition = intent.getIntExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,-1)
            Log.e("KELOMPOK_ITEM_POSITION", mKelompokListPosition.toString())
        }
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mCourseDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            Log.e("document", "document $mCourseDocumentId")
        }
        if (intent.hasExtra(Constants.NAME)){
            mGuruName = intent.getStringExtra(Constants.NAME)
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

    fun createKelompokInTopicList(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(FirestoreClass().getCurrentUserID())

        val kelompok = Kelompok(
            binding?.etNameDetails?.text.toString(),
            mKelompokImageURL,
            mGuruName,
            assignedUserArrayList,
            binding?.etCourseDetails?.text.toString(),
            binding?.etClassesDetails?.text.toString(),
        )
//        val kelompokList : ArrayList<Kelompok> = mCourseDetail.topicList[mTopicListPosition].kelompok
//
////        mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition] = kelompok

        mCourseDetail.topicList[mTopicListPosition].kelompok.removeAt(mKelompokListPosition)

        mCourseDetail.topicList[mTopicListPosition].kelompok.add(mKelompokListPosition, kelompok)

        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().addUpdateTopicList(this, mCourseDetail)
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

                createKelompokInTopicList()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(this@CourseKelompokDetailActivity,
                exception.message,
                Toast.LENGTH_LONG
            ).show()
            hideProgressDialog()
        }
    }

    fun addUpdateTopicListSuccess(){

        setResult(Activity.RESULT_OK)
        finish()
    }


}