    package com.example.kelomproapp.ui.activities

    import android.annotation.SuppressLint
    import android.app.Activity
    import android.app.DatePickerDialog
    import android.content.Context
    import android.content.Intent
    import android.database.Cursor
    import android.net.Uri
    import android.os.Bundle
    import android.provider.OpenableColumns
    import android.text.InputType
    import android.view.Menu
    import android.view.MenuItem
    import android.view.View
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.core.net.toUri
    import androidx.recyclerview.widget.GridLayoutManager
    import com.example.kelomproapp.R
    import com.example.kelomproapp.adapter.TaskAnggotaListItemsAdapter
    import com.example.kelomproapp.databinding.ActivityTaskDetailsBinding
    import com.example.kelomproapp.dialog.AnggotaListDialog
    import com.example.kelomproapp.firebase.FirestoreClass
    import com.example.kelomproapp.models.Kelompok
    import com.example.kelomproapp.models.SelectedAnggota
    import com.example.kelomproapp.models.Task
    import com.example.kelomproapp.models.Siswa
    import com.example.kelomproapp.utils.Constants
    import com.google.firebase.storage.FirebaseStorage
    import com.google.firebase.storage.StorageReference
    import java.text.SimpleDateFormat
    import java.util.*
    import kotlin.collections.ArrayList

    class TaskDetailsActivity : BaseActivity() {
        private var binding : ActivityTaskDetailsBinding? = null
        private lateinit var mKelompokDetails : Kelompok
        private lateinit var mAnggotaDetailList : ArrayList<Siswa>
        private var mTaskListPosition = -1
        private var mSelectedDueDateMilliSeconds : Long = 0
        private var mTaskId : String? = null

        private lateinit var storageReference: StorageReference
        private var uploadedPdfFileName: String = ""
        private var selectedPdfFileName: String = ""
        private var mDatabasePdf: Uri? = null
        private var mUploadedPdfUri: Uri? = null

        companion object {
            const val PICK_PDF_REQUEST_CODE = 1
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
            setContentView(binding?.root)

            setupActionBar()



            getIntentData()
            setupSelectedAnggotaList()

            binding?.etNameTaskDetails?.setText(mKelompokDetails
                .taskList[mTaskListPosition].name)

            binding?.etNameTaskDetails?.setSelection(binding?.etNameTaskDetails?.text.toString().length)

            binding?.tvSelectMembers?.setOnClickListener{
                anggotaListDialog()
            }

            val currentUserID = FirestoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                FirestoreClass().getUserRole(currentUserID) { role ->
                    if (role == "siswa") {
                        binding?.etNilai?.inputType = InputType.TYPE_NULL
                    }
                }
            }

            binding?.etNilai?.setText(mKelompokDetails
                .taskList[mTaskListPosition].nilai)

            mSelectedDueDateMilliSeconds =
                mKelompokDetails.taskList[mTaskListPosition].dueDate

            if (mSelectedDueDateMilliSeconds > 0 ){
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
                binding?.tvSelectDueDate?.text = selectedDate
            }

            binding?.tvSelectDueDate?.setOnClickListener {
                showDatePicker()
            }

            storageReference = FirebaseStorage.getInstance().reference

            val mUploadedPdfUriString: String? = mKelompokDetails.taskList[mTaskListPosition].pdfUrl
            mDatabasePdf = mUploadedPdfUriString?.toUri()

            binding?.btnUploadPdf?.setOnClickListener {
                showPdfChooser()
            }

            if (mDatabasePdf!= null) {
                selectedPdfFileName = mDatabasePdf.toString()
                binding?.textViewUploadedPdfName?.text = "$selectedPdfFileName"
            }

            binding?.textViewUploadedPdfName?.setOnClickListener {
                if (mDatabasePdf!= null) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = mDatabasePdf
                    startActivity(intent)
                }
            }



            binding?.btnUpdateCardDetails?.setOnClickListener {
                if (binding?.etNameTaskDetails?.text.toString().isNotEmpty()){
                    updateTaskDetails()
                }else{
                    Toast.makeText(this@TaskDetailsActivity,"masukkan nama tugas", Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_delete_task, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                R.id.action_delete_card -> {
                   alertDialogForDeleteTask (mKelompokDetails.taskList[mTaskListPosition].name)
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }

        private fun setupActionBar(){
            setSupportActionBar(binding?.toolbarDetailsKelompokActivity)
            val toolbar = supportActionBar
            if (toolbar != null){
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            }
            binding?.toolbarDetailsKelompokActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        private fun getIntentData() {
            if (intent.hasExtra(Constants.KELOMPOK_DETAIL)) {
                mKelompokDetails = intent.getParcelableExtra(Constants.KELOMPOK_DETAIL)!!
            }
            if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
                mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
            }
            if (intent.hasExtra(Constants.LIST_ANGGOTA_KELOMPOK)){
                mAnggotaDetailList = intent.getParcelableArrayListExtra(Constants.LIST_ANGGOTA_KELOMPOK)!!
            }
            if (intent.hasExtra(Constants.TASK_ID)){
                mTaskId = intent.getStringExtra(Constants.TASK_ID)
            }
        }

        private fun anggotaListDialog(){
            var taskAssignedAnggotaList = mKelompokDetails.taskList[mTaskListPosition].assignedTo

            if (taskAssignedAnggotaList.size > 0){
                for (i in mAnggotaDetailList.indices){
                    for (j in taskAssignedAnggotaList){
                        if (mAnggotaDetailList[i].id == j){
                            mAnggotaDetailList[i].selected = true
                        }
                    }
                }
            }else {
                for (i in taskAssignedAnggotaList.indices) {
                    mAnggotaDetailList[i].selected = false
                }
            }
        val listDialog = object : AnggotaListDialog(
            this,
            mAnggotaDetailList, "Pilih Anggota"
        ){
            override fun onItemSelected(siswa: Siswa, action: String) {
                if (action == Constants.SELECT){
                    if (!mKelompokDetails.taskList[mTaskListPosition].
                        assignedTo.contains(siswa.id)){
                        mKelompokDetails.taskList[mTaskListPosition].
                       assignedTo.add(siswa.id)
                    }
                }else{
                    mKelompokDetails.taskList[mTaskListPosition].
                    assignedTo.remove(siswa.id)

                    for (i in mAnggotaDetailList.indices){
                        if (mAnggotaDetailList[i].id == siswa.id){
                            mAnggotaDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedAnggotaList()
            }
        }
        listDialog.show()
        }

        private fun setupSelectedAnggotaList(){
            val taskAssignedMember =
                mKelompokDetails.taskList[mTaskListPosition].assignedTo


            val selectedAnggotaList : ArrayList<SelectedAnggota> = ArrayList()

            for (i in mAnggotaDetailList.indices){
                for (j in taskAssignedMember){
                    if (mAnggotaDetailList[i].id == j){
                        val selectedMember = SelectedAnggota(
                            mAnggotaDetailList[i].id,
                            mAnggotaDetailList[i].image
                        )
                        selectedAnggotaList.add(selectedMember)
                    }
                }
            }

            if (selectedAnggotaList.size > 0){
                selectedAnggotaList.add(SelectedAnggota("",""))
                binding?.tvSelectMembers?.visibility = View.GONE
                binding?.rvSelectedAnggotaList?.visibility = View.VISIBLE

                binding?.rvSelectedAnggotaList?.layoutManager = GridLayoutManager(
                    this,6
                )
                val adapter = TaskAnggotaListItemsAdapter(this,selectedAnggotaList,true)

                binding?.rvSelectedAnggotaList?.adapter = adapter
                adapter.setOnClickListener(
                    object : TaskAnggotaListItemsAdapter.OnClickListener{
                        override fun onClick() {
                            anggotaListDialog()
                        }

                    }
                )
            }else{
                binding?.tvSelectMembers?.visibility = View.VISIBLE
                binding?.rvSelectedAnggotaList?.visibility = View.GONE
            }
        }

        private fun deleteTask(){

            mKelompokDetails.taskList.removeAt(mTaskListPosition)

            FirestoreClass().addUpdateTaskList(this@TaskDetailsActivity,mKelompokDetails)
        }

        private fun alertDialogForDeleteTask(taskName : String) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert")
            builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card,taskName))
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Iya") { dialogInterface, which ->
                dialogInterface.dismiss() // Dialog will be dismissed
                deleteTask()
            }
            builder.setNegativeButton("Tidak") { dialogInterface, which ->
                dialogInterface.dismiss()
            }

            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        private fun updateTaskDetails(){
            val originalPdfUrl = mKelompokDetails.taskList[mTaskListPosition].pdfUrl
            val pdfUriString = mUploadedPdfUri?.toString() ?: originalPdfUrl

            val task = Task(
                name = binding?.etNameTaskDetails?.text.toString(),
                createdBy = mKelompokDetails.taskList[mTaskListPosition].createdBy,
                assignedTo =  mKelompokDetails.taskList[mTaskListPosition].assignedTo,
                dueDate =  mSelectedDueDateMilliSeconds,
                pdfUrl =  pdfUriString,
                nilai = binding?.etNilai?.text.toString()

            )


            // Remove the existing task at mTaskListPosition
            mKelompokDetails.taskList.removeAt(mTaskListPosition)

            // Add the updated task at the same position
            mKelompokDetails.taskList.add(mTaskListPosition, task)

            showProgressDialog(resources.getString(R.string.mohon_tunggu))
            FirestoreClass().addUpdateTaskList(this@TaskDetailsActivity,mKelompokDetails)
        }

        fun addUpdateTaskListSuccess(){

            setResult(Activity.RESULT_OK)
            finish()
        }

        private fun showDatePicker(){
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 0) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding?.tvSelectDueDate?.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)

                mSelectedDueDateMilliSeconds = theDate!!.time


            }       ,
                year,
                month,
                day
            )
            dpd.show()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                if (data != null) {

                    showProgressDialog(resources.getString(R.string.mohon_tunggu))
                    val selectedPdfUri: Uri? = data.data

                    // Mendapatkan nama file dari URI
                    val pdfFileName: String? = getFileName(selectedPdfUri)

                    // Menyimpan file PDF ke Firebase Storage
                    val pdfStorageReference: StorageReference =
                        storageReference.child("pdfs/$pdfFileName")
                    pdfStorageReference.putFile(selectedPdfUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            // File berhasil diunggah
                            pdfStorageReference.downloadUrl.addOnCompleteListener { uriTask ->
                                if (uriTask.isSuccessful) {
                                    selectedPdfFileName = pdfFileName ?: ""
                                    uploadedPdfFileName = selectedPdfFileName
                                    mUploadedPdfUri = uriTask.result
                                    binding?.textViewUploadedPdfName?.text =
                                        "File PDF terunggah: $selectedPdfFileName"
                                    hideProgressDialog()

                                    // Simpan informasi file PDF ke SharedPreferences atau ViewModel
                                    val sharedPref =
                                        getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    editor.putString("pdf_file_name", selectedPdfFileName)
                                    editor.putString("pdf_uri", mUploadedPdfUri.toString())
                                    editor.apply()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            // Gagal mengunggah file
                            binding?.textViewUploadedPdfName?.text = "Gagal mengunggah file."
                            hideProgressDialog()
                        }
                }
            }
        }

        @SuppressLint("Range")
        private fun getFileName(uri: Uri?): String? {
            var result: String? = null
            if (uri?.scheme == "content") {
                val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri?.lastPathSegment
            }
            return result
        }
        fun showPdfChooser(){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(intent, PICK_PDF_REQUEST_CODE)
        }

    }

