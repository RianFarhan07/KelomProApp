package com.example.kelomproapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.CourseItemsAdapter
import com.example.kelomproapp.adapter.TaskItemsAdapter
import com.example.kelomproapp.databinding.ActivityGuruCourseBinding
import com.example.kelomproapp.databinding.ActivityIntroBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Task
import com.example.kelomproapp.utils.Constants

class GuruCourseActivity : BaseActivity() {
    private var binding : ActivityGuruCourseBinding? = null
    private var mGuruName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGuruCourseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()

        FirestoreClass().getUserDetails(this,"guru",false)

        binding!!.tvAddCourse.setOnClickListener {
            binding!!.tvAddCourse.visibility = View.GONE
            binding!!.cvAddCourseListName.visibility = View.VISIBLE
        }

        binding!!.ibCloseListName.setOnClickListener {
            binding!!.tvAddCourse.visibility = View.VISIBLE
            binding!!.cvAddCourseListName.visibility = View.GONE
        }

        binding!!.ibDoneListName.setOnClickListener{
            val listName = binding!!.etCourseListName.text.toString()
            val listClasses = binding!!.etClassListName.text.toString()

            if (listName.isNotEmpty()){
                createCourseList(listName,listClasses,mGuruName!!)
                binding!!.etClassListName.text.clear()
                binding!!.etCourseListName.text.clear()
                binding!!.tvAddCourse.visibility = View.VISIBLE
                binding!!.cvAddCourseListName.visibility = View.GONE
            }else{
                Toast.makeText(this,"Please enter course name or class",
                    Toast.LENGTH_LONG).show()
            }
        }

        FirestoreClass().getCourseList(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCourseListActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = "Daftar Materi"
        }
        binding?.toolbarCourseListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getGuruName(guru: Guru) {
        // Set data guru ke UI sesuai kebutuhan
        mGuruName = guru.name
    }



    fun populateCourseListToUI(courseList: ArrayList<Course>){

        val rvCourseList : RecyclerView = findViewById(R.id.rv_course_list)
        val tvNoTaskAvailable : TextView = findViewById(R.id.tv_no_course_available)


        if (courseList.size > 0){
            rvCourseList.visibility = View.VISIBLE
            tvNoTaskAvailable.visibility  = View.GONE

            rvCourseList.layoutManager = GridLayoutManager(this,2)
            rvCourseList.setHasFixedSize(true)

            val adapter = CourseItemsAdapter(this,courseList)
            rvCourseList.adapter = adapter

            adapter.setOnClickListener(object: CourseItemsAdapter.OnClickListener{
                override fun onClick(position: Int,model: Course) {
                    val intent = Intent(this@GuruCourseActivity, GuruTopicActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    Log.e("KELOMPOK","ID : ${model.documentId}")
                    startActivity(intent)
                }
            })
        }else{
            rvCourseList.visibility = View.GONE
            tvNoTaskAvailable.visibility  = View.VISIBLE
        }
    }

    fun createCourseList(courseListName: String, courseClasses: String,guruName : String){
        val course = Course(
            name = courseListName,
            guru =  guruName,
            classes = courseClasses )

        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().createCourse(this,course)

    }

    fun courseCreatedSuccessfully(){
        hideProgressDialog()
        FirestoreClass().getCourseList(this)
    }
}