package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.TopicItemsAdapter
import com.example.kelomproapp.databinding.ActivityGuruTopicBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Topic
import com.example.kelomproapp.utils.Constants

class GuruTopicActivity : BaseActivity() {
    private var binding : ActivityGuruTopicBinding? = null
    private lateinit var mCourseDetail : Course
    private lateinit var mCourseDocumentId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGuruTopicBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mCourseDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            Log.e("document","document $mCourseDocumentId")


        } else {
            Log.e("document", "No document ID provided.")
            // Handle the case when no document ID is provided, such as showing an error message or finishing the activity.
            finish()
        }

        FirestoreClass().getCourseDetails(this@GuruTopicActivity, mCourseDocumentId)


        binding!!.tvAddTopic.setOnClickListener {
            binding!!.tvAddTopic.visibility = View.GONE
            binding!!.cvAddTopicListName.visibility = View.VISIBLE
        }

        binding!!.ibCloseListName.setOnClickListener {
            binding!!.tvAddTopic.visibility = View.VISIBLE
            binding!!.cvAddTopicListName.visibility = View.GONE
        }

        binding!!.ibDoneListName.setOnClickListener{
            val listName = binding!!.etTopicListName.text.toString()

            if (listName.isNotEmpty()){
                createTopicList(listName)
                binding!!.tvAddTopic.visibility = View.VISIBLE
                binding!!.cvAddTopicListName.visibility = View.GONE
            } else {
                Toast.makeText(this,"Please enter topic name",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTopikListActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = "Daftar Topic Matkul ${mCourseDetail.name}"
        }
        binding?.toolbarTopikListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun populateTopicListToUI(topicList: ArrayList<Topic>){
        val rvTopicList : RecyclerView = findViewById(R.id.rv_topic_list)
        val tvNoTopicAvailable : TextView = findViewById(R.id.tv_no_topic_available)

        if (topicList.size > 0){
            rvTopicList.visibility = View.VISIBLE
            tvNoTopicAvailable.visibility  = View.GONE

            rvTopicList.layoutManager = GridLayoutManager(this,2)
            rvTopicList.setHasFixedSize(true)

            val adapter = TopicItemsAdapter(this, topicList)
            rvTopicList.adapter = adapter

            adapter.setOnClickListener(object: TopicItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Topic) {
                   topicDetails(position)
                }
            })
        } else {
            rvTopicList.visibility = View.GONE
            tvNoTopicAvailable.visibility  = View.VISIBLE
        }
    }



    fun createTopicList(topicListName: String){
        val topic = Topic(name = topicListName)
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        mCourseDetail.topicList.add(0,topic)
        FirestoreClass().addUpdateTopicList(this, mCourseDetail)
    }

    fun topicCreatedSuccessfully(){
        hideProgressDialog()
        FirestoreClass().getCourseDetails(this@GuruTopicActivity, mCourseDetail.documentId.toString())
    }

    fun CourseDetails(course: Course){
        mCourseDetail = course
        setupActionBar()
//        hideProgressDialog()
//        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        populateTopicListToUI(mCourseDetail.topicList)
    }

    fun topicDetails(topicListPosition: Int){
        val intent = Intent(this, KelompokCourseActivity::class.java)
        intent.putExtra(Constants.TOPIC_LIST_ITEM_POSITION,topicListPosition)
        intent.putExtra(Constants.COURSE_DETAIL,mCourseDetail)
        intent.putExtra(Constants.DOCUMENT_ID, mCourseDocumentId)
        startActivity(intent)
    }


}
