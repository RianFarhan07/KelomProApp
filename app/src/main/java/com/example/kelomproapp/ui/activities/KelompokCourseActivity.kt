package com.example.kelomproapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.adapter.TopicItemsAdapter
import com.example.kelomproapp.databinding.ActivityKelompokCourseBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.*
import com.example.kelomproapp.utils.Constants
import kotlinx.android.synthetic.main.activity_kelompok_course.*

class KelompokCourseActivity : BaseActivity() {
    private var binding : ActivityKelompokCourseBinding? = null
    private var mGuruName : String? = null
    private var mSiswaName : String? = null
    private lateinit var mCourseDetail: Course
    private lateinit var mTopicDetail: Topic
    private lateinit var mKelompokDetail: Kelompok
    private lateinit var mCourseDocumentId : String
    private var mTopicListPosition = -1
    private var mKelompokListPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityKelompokCourseBinding.inflate(layoutInflater)
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
            startActivity(intent)
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
        }
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mCourseDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            Log.e("document", "document $mCourseDocumentId")
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

            val adapter = KelompokItemsAdapter(this,kelompokList)
            rvKelompokList.adapter = adapter

            adapter.setOnClickListener(object: KelompokItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Kelompok) {
                    val intent = Intent(this@KelompokCourseActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    Log.e("KELOMPOK","ID : ${model.documentId}")
                    startActivityForResult(intent, MainActivity.UPDATE_KELOMPOK_REQUEST_CODE)
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

//    fun KelompokDetails(topicListPosition: Int, kelompokPosition: Int){
//        val intent = Intent(this, TaskCourseActivity::class.java)
//        intent.putExtra(Constants.TOPIC_LIST_ITEM_POSITION,topicListPosition)
//        intent.putExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,kelompokPosition)
//        intent.putExtra(Constants.COURSE_DETAIL,mCourseDetail)
//        intent.putExtra(Constants.DOCUMENT_ID, mCourseDocumentId)
//        startActivity(intent)
//    }



}