    package com.example.kelomproapp.ui.activities

    import android.content.Intent
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
    import com.example.kelomproapp.adapter.TaskItemsAdapter
    import com.example.kelomproapp.databinding.ActivityTaskListBinding
    import com.example.kelomproapp.firebase.FirestoreClass
    import com.example.kelomproapp.models.Kelompok
    import com.example.kelomproapp.models.Task
    import com.example.kelomproapp.models.Siswa
    import com.example.kelomproapp.utils.Constants

    class TaskListActivity : BaseActivity() {

        private var binding : ActivityTaskListBinding? = null
        private lateinit var mKelompokDetails : Kelompok
        private lateinit var mKelompokDocumentId : String
        lateinit var mAssignedAnggotaDetailList: ArrayList<Siswa>

        companion object {
            const val TASK_DETAILS_REQUEST_CODE : Int = 15
            const val ANGGOTA_DETAILS_REQUEST_CODE : Int = 14
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            binding = ActivityTaskListBinding.inflate(layoutInflater)
            super.onCreate(savedInstanceState)
            setContentView(binding?.root)

            if (intent.hasExtra(Constants.DOCUMENT_ID)){
                mKelompokDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            }

            showProgressDialog(resources.getString(R.string.mohon_tunggu))
            FirestoreClass().getKelompokDetails(this,mKelompokDocumentId)


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
                    createTaskList(listName)
                    binding!!.etTaskListName.text.clear()
                    binding!!.tvAddTugas.visibility = View.VISIBLE
                    binding!!.cvAddTaskListName.visibility = View.GONE
                }else{
                    Toast.makeText(this,"Please enter list name",
                        Toast.LENGTH_LONG).show()
                }
            }

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == TASK_DETAILS_REQUEST_CODE || resultCode == ANGGOTA_DETAILS_REQUEST_CODE) {
                showProgressDialog(resources.getString(R.string.mohon_tunggu))
                FirestoreClass().getKelompokDetails(this, mKelompokDocumentId)
            } else {
                Log.e("cancelled", "cancelled")
            }
        }
        private fun setupActionBar(){
            setSupportActionBar(binding?.toolbarTaskListActivity)
            val toolbar = supportActionBar
            if (toolbar != null){
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
                supportActionBar?.title = "Daftar Tugas ${mKelompokDetails.name}"
            }
            binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
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
                    intent.putExtra(Constants.DOCUMENT_ID,mKelompokDocumentId)
                    startActivity(intent)

                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }

        fun kelompokDetails(kelompok: Kelompok){
            mKelompokDetails = kelompok

            setupActionBar()
            hideProgressDialog()

            showProgressDialog(resources.getString(R.string.mohon_tunggu))
            FirestoreClass().getAssignedAnggotaListDetails(this,mKelompokDetails.assignedTo)


        }


        fun addUpdateTaskListSuccess(){
            hideProgressDialog()

            showProgressDialog(resources.getString(R.string.mohon_tunggu))
            FirestoreClass().getKelompokDetails(this@TaskListActivity,mKelompokDetails.documentId.toString())
        }

        fun createTaskList(taskListName: String){
            val taskAssignedUserList : ArrayList<String> = ArrayList()

            taskAssignedUserList.add(FirestoreClass().getCurrentUserID())


            val task = Task(taskListName, FirestoreClass().getCurrentUserID(),taskAssignedUserList)
            mKelompokDetails.taskList.add(0,task)
    //        mKelompokDetails.taskList.removeAt(mKelompokDetails.taskList.size -1)

            showProgressDialog(resources.getString(R.string.mohon_tunggu))

            FirestoreClass().addUpdateTaskList(this,mKelompokDetails)
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

                val adapter = TaskItemsAdapter(this,taskList)
                rvTaskList.adapter = adapter

                adapter.setOnClickListener(object: TaskItemsAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        taskDetails(position)
                    }
                })
            }else{
                rvTaskList.visibility = View.GONE
                tvNoTaskAvailable.visibility  = View.VISIBLE
            }
        }

        fun taskDetails(taskListPosition: Int){
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra(Constants.KELOMPOK_DETAIL,mKelompokDetails)
            intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
            intent.putExtra(Constants.LIST_ANGGOTA_KELOMPOK,mAssignedAnggotaDetailList)
            startActivityForResult(intent, TASK_DETAILS_REQUEST_CODE)
        }

        fun anggotaKelompokDetailList(list: ArrayList<Siswa>){
            mAssignedAnggotaDetailList = list

            hideProgressDialog()

            populateTaskListToUI(mKelompokDetails.taskList)

        }
    }