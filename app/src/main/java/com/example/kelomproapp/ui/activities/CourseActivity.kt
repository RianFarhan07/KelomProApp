package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.CourseItemsAdapter
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.databinding.ActivityCourseBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class CourseActivity : BaseActivity() {
    private var binding : ActivityCourseBinding? = null
    private var mGuruName : String? = null
    private var mSiswaName : String? = null
    private var mListClasses : String? = null

    companion object{
        const val CREATE_KELOMPOK_REQUEST_CODE = 77
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        FirestoreClass().getUserDetails(this,"guru",false)
        FirestoreClass().getUserDetails(this,"siswa",false)

        binding!!.tvAddCourse.setOnClickListener {
            val currentUserID = FirestoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                FirestoreClass().getUserRole(currentUserID) { role ->
                    if (role == "siswa") {
                        binding?.tvAddCourse?.text = "Buat Kelompok"
                        val intent = Intent(this, CreateKelompokActivity::class.java)
                        intent.putExtra(Constants.NAME, mSiswaName)
                        intent.putExtra(Constants.TO_COURSE, false)
                        startActivityForResult(intent, CREATE_KELOMPOK_REQUEST_CODE)
                    }else{
                        binding!!.tvAddCourse.visibility = View.GONE
                        binding!!.cvAddCourseListName.visibility = View.VISIBLE
                    }
                }
            }

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
                    FirestoreClass().getKelompokList(this)
                }else{
                    FirestoreClass().getCourseList(this)
                }
            }
        }


    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCourseListActivity)
        val toolbar = supportActionBar
        if (toolbar != null){val currentUserID = FirestoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                FirestoreClass().getUserRole(currentUserID) { role ->
                    if (role == "siswa") {
                        binding?.tvTitle?.text = "Daftar Kelompok"
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if (resultCode == Activity.RESULT_OK && requestCode == CREATE_KELOMPOK_REQUEST_CODE){
            FirestoreClass().getKelompokList(this)
        }
        else{
            Log.e("cancelled","cancelled")
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

    fun getSiswaName(siswa: Siswa) {
        // Set data guru ke UI sesuai kebutuhan
        mSiswaName = "${siswa.firstName} ${siswa.lastName}"
    }

    fun populateKelompokListToUI(kelompokList: ArrayList<Kelompok>){

        val rvKelompokList : RecyclerView = findViewById(R.id.rv_course_list)
        val tvNoKelompokAvailable : TextView = findViewById(R.id.tv_no_course_available)

        hideProgressDialog()

        if (kelompokList.size >0){
            rvKelompokList.visibility = View.VISIBLE
            tvNoKelompokAvailable.visibility  = View.GONE

            rvKelompokList.layoutManager = LinearLayoutManager(this)
            rvKelompokList.setHasFixedSize(true)

            val adapter = KelompokItemsAdapter(this,kelompokList)
            rvKelompokList.adapter = adapter

            adapter.setOnClickListener(object: KelompokItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Kelompok) {
                    val intent = Intent(this@CourseActivity, TaskListActivity::class.java)
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