package com.example.kelomproapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.adapter.TopicItemsAdapter
import com.example.kelomproapp.databinding.ActivityKelompokCourseBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Topic
import com.example.kelomproapp.utils.Constants
import kotlinx.android.synthetic.main.activity_kelompok_course.*

class KelompokCourseActivity : BaseActivity() {
    private var binding : ActivityKelompokCourseBinding? = null
    private lateinit var mUserName : String
    private lateinit var mCourseDetail: Course
    private lateinit var mTopicDetail: Topic
    private lateinit var mCourseDocumentId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityKelompokCourseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mCourseDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            Log.e("document","document $mCourseDocumentId")

            FirestoreClass().getCourseDetails(this@KelompokCourseActivity, mCourseDocumentId)
        } else {
            Log.e("document", "No document ID provided.")
            // Handle the case when no document ID is provided, such as showing an error message or finishing the activity.
            finish()
        }

        binding?.fabCreateKelompok?.setOnClickListener{
            val intent = Intent(this, CreateKelompokActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            intent.putExtra(Constants.TO_COURSE,true)
        }

    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarKelompokCourseActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = "Daftar Topic"
        }
        binding?.toolbarKelompokCourseActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun populateKelompokListToUI(kelompokList: ArrayList<Kelompok>){
        val rvKelompokList : RecyclerView = findViewById(R.id.rv_kelompok_list)
        val tvNoTopicAvailable : TextView = findViewById(R.id.tv_no_kelompok_available)

        if (kelompokList.size > 0){
            rvKelompokList.visibility = View.VISIBLE
            tvNoTopicAvailable.visibility  = View.GONE

            rvKelompokList.layoutManager = GridLayoutManager(this,2)
            rv_kelompok_list.setHasFixedSize(true)

            val adapter = KelompokItemsAdapter(this, kelompokList)
            rvKelompokList.adapter = adapter

//            adapter.setOnClickListener(object: TopicItemsAdapter.OnClickListener{
//                override fun onClick(position: Int, model: Topic) {
//                    val intent = Intent(this@KelompokCourseActivity, TaskListCourseActivity::class.java)
//                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
//                    Log.e("KELOMPOK", "ID : ${model.documentId}")
//                    startActivity(intent)
//                }
//            })
        } else {
            rvKelompokList.visibility = View.GONE
            tvNoTopicAvailable.visibility  = View.VISIBLE
        }
    }

    fun courseDetails(course: Course){
        mCourseDetail = course

        setupActionBar()

//        showProgressDialog(resources.getString(R.string.please_wait))
    }

    fun TopicDetails(topic: Topic){
        mTopicDetail = topic
        setupActionBar()
//        hideProgressDialog()
//        showProgressDialog(resources.getString(R.string.mohon_tunggu))
//        populateKelompokListToUI(mCourseDetail.topicList[.)
    }



}