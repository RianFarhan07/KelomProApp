package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.TaskInCourseAdapter
import com.example.kelomproapp.adapter.TaskItemsAdapter
import com.example.kelomproapp.databinding.ActivityCourseTaskBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.*
import com.example.kelomproapp.utils.Constants

class CourseTaskActivity : BaseActivity() {

    private var binding : ActivityCourseTaskBinding? = null
    private lateinit var mCourseDetail: Course
    private lateinit var mTopicDetail: Topic
    private lateinit var mKelompokDetail: Kelompok
    private lateinit var mCourseDocumentId : String
    private var mTopicListPosition = -1
    private var mKelompokListPosition = -1
    private var mTaskListPosition = -1
    lateinit var mAssignedAnggotaDetailList: ArrayList<Siswa>

    companion object {
        const val UPDATE_KELOMPOK_REQUEST_CODE : Int = 20
        const val ANGGOTA_DETAILS_REQUEST_CODE : Int = 21
        const val UPDATE_TASK_REQUEST_CODE : Int = 22
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseTaskBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()
        FirestoreClass().getCourseDetails(this,mCourseDocumentId)

        binding!!.tvAddTugas.setOnClickListener {
            binding!!.tvAddTugas.visibility = View.GONE
            binding!!.cvAddTaskListName.visibility = View.VISIBLE
        }

        binding!!.ibCloseListName.setOnClickListener {
            binding!!.tvAddTugas.visibility = View.VISIBLE
            binding!!.cvAddTaskListName.visibility = View.GONE
        }

        binding!!.ibDoneListName.setOnClickListener{
            val listName = binding!!.etTaskListName.text.toString()

            if (listName.isNotEmpty()){
                createTugasList(listName)
                binding!!.tvAddTugas.visibility = View.VISIBLE
                binding!!.cvAddTaskListName.visibility = View.GONE
            } else {
                Toast.makeText(this,"Please enter task name",
                    Toast.LENGTH_LONG).show()
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
        if (intent.hasExtra(Constants.LIST_ANGGOTA_KELOMPOK)){
            mAssignedAnggotaDetailList = intent.getParcelableArrayListExtra(Constants.LIST_ANGGOTA_KELOMPOK)!!
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_TASK_REQUEST_CODE){
            FirestoreClass().getCourseDetails(this,mCourseDocumentId)
        }
        else{
            Log.e("cancelled","cancelled")
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
                val intent = Intent(this, CourseKelompokDetailActivity::class.java)
                intent.putExtra(Constants.DOCUMENT_ID,mCourseDocumentId)
                intent.putExtra(Constants.TOPIC_LIST_ITEM_POSITION,mTopicListPosition)
                intent.putExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,mKelompokListPosition)
                intent.putExtra(Constants.COURSE_DETAIL,mCourseDetail)
                startActivityForResult(intent,UPDATE_KELOMPOK_REQUEST_CODE)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun CourseDetails(course: Course){
        mCourseDetail = course
        setupActionBar()
//        hideProgressDialog()
//        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        FirestoreClass().getAssignedAnggotaListDetails(this,
            mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].assignedTo)
    }

    fun populateTaskListToUI(taskList: ArrayList<Task>){

        val rvTaskList : RecyclerView = findViewById(R.id.rv_task_list)
        val tvNoTaskAvailable : TextView = findViewById(R.id.tv_no_task_available)


        if (taskList.size > 0){
            rvTaskList.visibility = View.VISIBLE
            tvNoTaskAvailable.visibility  = View.GONE

            rvTaskList.layoutManager = LinearLayoutManager(this)
            rvTaskList.setHasFixedSize(true)

            val adapter = TaskInCourseAdapter(this,taskList)
            rvTaskList.adapter = adapter

            adapter.setOnClickListener(object: TaskInCourseAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    taskDetails(position)
                }
            })
        }else{
            rvTaskList.visibility = View.GONE
            tvNoTaskAvailable.visibility  = View.VISIBLE
        }
    }

    fun createTugasList(tugasListName: String){
        val tugas = Task(name = tugasListName)
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList.add(0,tugas)
        FirestoreClass().addUpdateTopicList(this, mCourseDetail)
    }

    fun courseCreatedSuccessfully(){
        hideProgressDialog()
        populateTaskListToUI(
            mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList)
    }

    fun anggotaKelompokDetailList(list: ArrayList<Siswa>){
        mAssignedAnggotaDetailList = list


        populateTaskListToUI(mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList)

    }

    fun taskDetails(taskPosition: Int){
        val intent = Intent(this, CourseTaskDetailActivity::class.java)
        intent.putExtra(Constants.TOPIC_LIST_ITEM_POSITION,mTopicListPosition)
        intent.putExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,mKelompokListPosition)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskPosition)
        intent.putExtra(Constants.COURSE_DETAIL,mCourseDetail)
        intent.putExtra(Constants.DOCUMENT_ID, mCourseDocumentId)
        intent.putExtra(Constants.LIST_ANGGOTA_KELOMPOK,mAssignedAnggotaDetailList)
        startActivityForResult(intent,UPDATE_TASK_REQUEST_CODE)
    }


}