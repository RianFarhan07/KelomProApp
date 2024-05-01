package com.example.kelomproapp.ui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.CourseItemsAdapter
import com.example.kelomproapp.databinding.ActivityCourseBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class CourseActivity : BaseActivity() {
    private var binding : ActivityCourseBinding? = null
    private var mGuruName : String? = null
    private var mListClasses : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
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

            // Validasi mListClasses sebelum digunakan
            if (listName.isNotEmpty() && mListClasses!!.isNotEmpty() && mGuruName != null){
                createCourseList(listName, mListClasses!!, mGuruName!!)
                binding!!.etCourseListName.text.clear()
                binding!!.tvAddCourse.visibility = View.VISIBLE
                binding!!.cvAddCourseListName.visibility = View.GONE
            } else {
                Toast.makeText(this,"Please enter course name or class",
                    Toast.LENGTH_LONG).show()
            }
        }



        binding?.etClassListName?.setOnClickListener{
            showClassSelectionDialog(this)
        }
        val currentUserID = FirestoreClass().getCurrentUserID()
        if (currentUserID.isNotEmpty()) {
            FirestoreClass().getUserRole(currentUserID) { role ->
                if (role == "siswa") {

                    FirestoreClass().getCourseListClasses(this)
                    binding?.tvAddCourse?.visibility = View.GONE
                    binding?.cvAddCourseListName?.visibility = View.GONE
                }else{
                    FirestoreClass().getCourseList(this)
                }
            }
        }


    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCourseListActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.title = "Daftar Materi"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_home_course, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.action_home -> {
                val currentUserID = FirestoreClass().getCurrentUserID()
                if (currentUserID.isNotEmpty()) {
                    FirestoreClass().getUserRole(currentUserID) { role ->
                        if (role == "siswa") {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else if (role == "guru") {
                            val intent = Intent(this, MainGuruActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getGuruName(guru: Guru) {
        // Set data guru ke UI sesuai kebutuhan
        mGuruName = guru.name
    }



    fun populateCourseListToUI(courseList: ArrayList<Course>){
        hideProgressDialog()
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
                    val intent = Intent(this@CourseActivity, CourseTopicActivity::class.java)
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
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(FirestoreClass().getCurrentUserID())

        val course = Course(
            name = courseListName,
            guru =  guruName,
            assignedTo = assignedUserArrayList,
            classes = courseClasses )

        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().createCourse(this,course)

    }

    fun courseCreatedSuccessfully(){
        hideProgressDialog()
        FirestoreClass().getCourseList(this)
    }

    fun showClassSelectionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pilih Kelas")

        // Inisialisasi RadioGroup
        val radioGroup = RadioGroup(context)
        radioGroup.orientation = RadioGroup.VERTICAL

        // Array dengan daftar kelas yang tersedia
        val classes = arrayOf("X", "XI", "XII")

        // Tambahkan radio button untuk setiap kelas ke dalam RadioGroup
        for (i in classes.indices) {
            val radioButton = RadioButton(context)
            radioButton.text = classes[i]
            radioButton.id = i
            radioGroup.addView(radioButton)
        }

        builder.setView(radioGroup)

        // Set action ketika radio button dipilih
        builder.setPositiveButton("Pilih") { dialog, _ ->
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedClass = classes[selectedRadioButtonId]
            dialog.dismiss()

            binding?.etClassListName?.text = selectedClass
            mListClasses = selectedClass

        }

        // Set action ketika dialog dibatalkan
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        // Tampilkan dialog
        builder.create().show()
    }
}