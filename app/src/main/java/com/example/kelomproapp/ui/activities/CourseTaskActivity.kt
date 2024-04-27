package com.example.kelomproapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityCourseTaskBinding
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.models.Topic
import com.example.kelomproapp.utils.Constants

class CourseTaskActivity : AppCompatActivity() {

    private var binding : ActivityCourseTaskBinding? = null
    private lateinit var mCourseDetail: Course
    private lateinit var mTopicDetail: Topic
    private lateinit var mKelompokDetail: Kelompok
    private lateinit var mCourseDocumentId : String
    private var mTopicListPosition = -1
    private var mKelompokListPosition = -1
    lateinit var mAssignedAnggotaDetailList: ArrayList<Siswa>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseTaskBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()
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
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTaskCourseActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = "Daftar Tugas Kelompok ${
                mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].name}"
        }
        binding?.toolbarTaskCourseActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_edit_kelompok -> {
                val intent = Intent(this, KelompokDetailsActivity::class.java)
                intent.putExtra(Constants.DOCUMENT_ID,mCourseDocumentId)
                startActivity(intent)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}