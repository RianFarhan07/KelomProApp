package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokInCourseAdapter
import com.example.kelomproapp.databinding.ActivityCourseKelompokBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.*
import com.example.kelomproapp.utils.Constants

class CourseKelompokActivity : BaseActivity() {
    private var binding : ActivityCourseKelompokBinding? = null
    private var mGuruName : String? = null
    private var mSiswaName : String? = null
    private lateinit var mCourseDetail: Course
    private lateinit var mTopicDetail: Topic
    private lateinit var mKelompokDetail: Kelompok
    private lateinit var mCourseDocumentId : String
    private var mTopicListPosition = -1
    private var mKelompokListPosition = -1

    companion object {
        const val UPDATE_KELOMPOK_REQUEST_CODE : Int = 20
        const val ANGGOTA_DETAILS_REQUEST_CODE : Int = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseKelompokBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()
        FirestoreClass().getUserDetails(this,"guru",false)
        FirestoreClass().getCourseDetails(this,mCourseDocumentId)

        binding?.fabCreateKelompok?.setOnClickListener{
            val intent = Intent(this, CreateKelompokActivity::class.java)

            intent.putExtra(Constants.NAME,mGuruName)
            intent.putExtra(Constants.TO_COURSE,true)
            intent.putExtra(Constants.COURSE_DETAIL,mCourseDetail)
            intent.putExtra(Constants.TOPIC_LIST_ITEM_POSITION,mTopicListPosition)
            startActivityForResult(intent, UPDATE_KELOMPOK_REQUEST_CODE)
        }

    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarKelompokCourseActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = "Daftar Kelompok ${mCourseDetail.topicList[mTopicListPosition].name}"
        }
        binding?.toolbarKelompokCourseActivity?.setNavigationOnClickListener {
            onBackPressed()
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
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mCourseDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            Log.e("document", "document $mCourseDocumentId")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_KELOMPOK_REQUEST_CODE){
            FirestoreClass().getCourseDetails(this,mCourseDocumentId)
        }
        else{
            Log.e("cancelled","cancelled")
        }
    }

    fun getGuruName(guru: Guru) {
        // Set data guru ke UI sesuai kebutuhan
        mGuruName = guru.name
    }

    fun populateKelompokListToUI(kelompokList: ArrayList<Kelompok>){
        val rvKelompokList : RecyclerView = findViewById(R.id.rv_kelompok_list)
        val tvNoKelompokAvailable : TextView = findViewById(R.id.tv_no_kelompok_available)

        if (kelompokList.size >0){
            rvKelompokList.visibility = View.VISIBLE
            tvNoKelompokAvailable.visibility  = View.GONE

            rvKelompokList.layoutManager = LinearLayoutManager(this)
            rvKelompokList.setHasFixedSize(true)

            val adapter = KelompokInCourseAdapter(this,kelompokList)
            rvKelompokList.adapter = adapter

            adapter.setOnClickListener(object: KelompokInCourseAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    KelompokDetails(position)
                }
            })
        }else{
            rvKelompokList.visibility = View.GONE
            tvNoKelompokAvailable.visibility  = View.VISIBLE
        }
    }

    fun CourseDetails(course: Course){
        mCourseDetail = course
        setupActionBar()
//        hideProgressDialog()
//        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        populateKelompokListToUI(mCourseDetail.topicList[mTopicListPosition].kelompok)
    }

    fun TopicDetails(topic: Topic){
        mTopicDetail = topic
//        hideProgressDialog()
//        showProgressDialog(resources.getString(R.string.mohon_tunggu))
//        populateKelompokListToUI(mCourseDetail.topicList[.)
    }

    fun KelompokDetails(kelompokPosition: Int){
        val intent = Intent(this, CourseTaskActivity::class.java)
        intent.putExtra(Constants.TOPIC_LIST_ITEM_POSITION,mTopicListPosition)
        intent.putExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,kelompokPosition)
        intent.putExtra(Constants.COURSE_DETAIL,mCourseDetail)
        intent.putExtra(Constants.NAME,mGuruName)
        intent.putExtra(Constants.DOCUMENT_ID, mCourseDocumentId)
        startActivityForResult(intent, UPDATE_KELOMPOK_REQUEST_CODE)
    }



}