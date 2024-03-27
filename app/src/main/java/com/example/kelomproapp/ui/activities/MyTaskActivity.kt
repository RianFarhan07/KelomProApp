package com.example.kelomproapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.MyTaskItemsAdapter
import com.example.kelomproapp.adapter.TaskItemsAdapter
import com.example.kelomproapp.databinding.ActivityMyTaskBinding
import com.example.kelomproapp.databinding.ActivityTaskListBinding
import com.example.kelomproapp.models.Task
import com.example.kelomproapp.utils.Constants
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa

class MyTaskActivity : BaseActivity() {
    private var binding : ActivityMyTaskBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyTaskBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()
        getAssignedTaskList()

    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarMyTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding?.toolbarMyTaskListActivity?.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAssignedTaskList() {
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().getAssignedTaskList(this)
    }

    fun populateTaskListToUI(taskList: ArrayList<Task>){

        val rvTaskList : RecyclerView = findViewById(R.id.rv_task_list)
        val tvNoTaskAvailable : TextView = findViewById(R.id.tv_no_task_available)

        hideProgressDialog()

        if (taskList.size > 0){
            rvTaskList.visibility = View.VISIBLE
            tvNoTaskAvailable.visibility  = View.GONE

            rvTaskList.layoutManager = LinearLayoutManager(this)
            rvTaskList.setHasFixedSize(true)

            val adapter = MyTaskItemsAdapter(this, taskList)

            rvTaskList.adapter = adapter
        } else {
            rvTaskList.visibility = View.GONE
            tvNoTaskAvailable.visibility  = View.VISIBLE
        }
    }



}
