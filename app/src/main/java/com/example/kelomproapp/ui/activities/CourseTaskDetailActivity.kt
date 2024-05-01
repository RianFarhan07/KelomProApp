package com.example.kelomproapp.ui.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.TaskAnggotaListItemsAdapter
import com.example.kelomproapp.databinding.ActivityCourseKelompokDetailBinding
import com.example.kelomproapp.databinding.ActivityCourseTaskDetailBinding
import com.example.kelomproapp.dialog.AnggotaListDialog
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.*
import com.example.kelomproapp.utils.AlarmReceiver
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CourseTaskDetailActivity : BaseActivity() {

    private var binding : ActivityCourseTaskDetailBinding? = null
    private lateinit var mCourseDetails : Course
    private lateinit var mCourseDocumentId : String
    private var mSelectedImageFileUri : Uri? = null
    private var mKelompokImageURL : String = ""
    private var mGuruName : String? = null
    private var mTopicListPosition = -1
    private var mKelompokListPosition = -1
    private var mTaskListPosition = -1
    private var mSelectedDueDateMilliSeconds : Long = 0
    lateinit var mAssignedAnggotaDetailList: ArrayList<Siswa>

    private lateinit var storageReference: StorageReference
    private var uploadedPdfFileName: String = ""
    private var selectedPdfFileName: String = ""
    private var mDatabasePdf: Uri? = null
    private var mUploadedPdfUri: Uri? = null

    private lateinit var alarmManager : AlarmManager
    private lateinit var pendingIntent: PendingIntent

    companion object {
        const val PICK_PDF_REQUEST_CODE = 99
        const val ANGGOTA_EDIT = 98
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseTaskDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        getIntentData()
        setupActionBar()
        setupSelectedAnggotaList()

        binding?.etNameTaskDetails?.setText(
            mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].name)

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

        binding?.etNilai?.setText(
            mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].nilai)

        mSelectedDueDateMilliSeconds =
            mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].dueDate

        if (mSelectedDueDateMilliSeconds > 0 ){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            binding?.tvSelectDueDate?.text = selectedDate
        }

        binding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
        }

        storageReference = FirebaseStorage.getInstance().reference

        val mUploadedPdfUriString: String? =
            mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].pdfUrl
        mDatabasePdf = mUploadedPdfUriString?.toUri()

        binding?.btnUploadPdf?.setOnClickListener {
            showPdfChooser()
        }

        if (mDatabasePdf!= null) {
            selectedPdfFileName = mDatabasePdf.toString()
            binding?.textViewUploadedPdfName?.text =
                mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].pdfUrlName
        }else{
            binding?.textViewUploadedPdfName?.text = "Belum ada file terupload"
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
                Toast.makeText(this@CourseTaskDetailActivity,"masukkan nama tugas", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.COURSE_DETAIL)) {
            mCourseDetails = intent.getParcelableExtra(Constants.COURSE_DETAIL)!!
            Log.e("COURSE_DETAIL", mCourseDetails.toString())
        }
        if (intent.hasExtra(Constants.TOPIC_LIST_ITEM_POSITION)) {
            mTopicListPosition = intent.getIntExtra(Constants.TOPIC_LIST_ITEM_POSITION, -1)
            Log.e("TOPIC_ITEM_POSITION", mTopicListPosition.toString())
        }
        if (intent.hasExtra(Constants.KELOMPOK_LIST_ITEM_POSITION)){
            mKelompokListPosition = intent.getIntExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,-1)
            Log.e("KELOMPOK_ITEM_POSITION", mKelompokListPosition.toString())
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
            Log.e("KELOMPOK_ITEM_POSITION", mTaskListPosition.toString())
        }
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mCourseDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            Log.e("document", "document $mCourseDocumentId")
        }
        if (intent.hasExtra(Constants.LIST_ANGGOTA_KELOMPOK)){
            mAssignedAnggotaDetailList = intent.getParcelableArrayListExtra(Constants.LIST_ANGGOTA_KELOMPOK)!!
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarKelompokCourseActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = "Detail Tugas ${
                mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].name}"
        }
        binding?.toolbarKelompokCourseActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_task, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card -> {
                alertDialogForDeleteTask ( mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].name)
                return true
            }
            R.id.action_notif -> {
                if(mSelectedDueDateMilliSeconds > 0){
                    setAlarm()
                }else{
                    Toast.makeText(this,"Belum Ada Tenggat Waktu",Toast.LENGTH_LONG).show()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun anggotaListDialog(){
        var taskAssignedAnggotaList =
            mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].assignedTo

        if (taskAssignedAnggotaList.size > 0){
            for (i in mAssignedAnggotaDetailList.indices){
                for (j in taskAssignedAnggotaList){
                    if (mAssignedAnggotaDetailList[i].id == j){
                        mAssignedAnggotaDetailList[i].selected = true
                    }
                }
            }
        }else {
            for (i in taskAssignedAnggotaList.indices) {
                mAssignedAnggotaDetailList[i].selected = false
            }
        }
        val listDialog = object : AnggotaListDialog(
            this,
            mAssignedAnggotaDetailList, "Pilih Anggota"
        ){
            override fun onItemSelected(siswa: Siswa, action: String) {
                if (action == Constants.SELECT){
                    if (!mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].
                        assignedTo.contains(siswa.id)){
                        mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].
                        assignedTo.add(siswa.id)
                    }
                }else{
                    mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].
                    assignedTo.remove(siswa.id)

                    for (i in mAssignedAnggotaDetailList.indices){
                        if (mAssignedAnggotaDetailList[i].id == siswa.id){
                            mAssignedAnggotaDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedAnggotaList()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedAnggotaList(){
        hideProgressDialog()
        val taskAssignedMember =
            mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].
            assignedTo

        val selectedAnggotaList : ArrayList<SelectedAnggota> = ArrayList()

        for (i in mAssignedAnggotaDetailList.indices){
            for (j in taskAssignedMember){
                if (mAssignedAnggotaDetailList[i].id == j){
                    val selectedMember = SelectedAnggota(
                        mAssignedAnggotaDetailList[i].id,
                        mAssignedAnggotaDetailList[i].image
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

        mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList.removeAt(mTaskListPosition)

        FirestoreClass().addUpdateTopicList(this@CourseTaskDetailActivity,mCourseDetails)
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
        val originalPdfUrl = mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].pdfUrl
        val pdfUriString = mUploadedPdfUri?.toString() ?: originalPdfUrl

        val task = Task(
            name = binding?.etNameTaskDetails?.text.toString(),
            createdBy = mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].createdBy,
            assignedTo =  mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList[mTaskListPosition].assignedTo,
            dueDate =  mSelectedDueDateMilliSeconds,
            pdfUrl =  pdfUriString,
            pdfUrlName= selectedPdfFileName,
            nilai = binding?.etNilai?.text.toString()

        )


        // Remove the existing task at mTaskListPosition
        mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList.removeAt(mTaskListPosition)

        // Add the updated task at the same position
        mCourseDetails.topicList[mTopicListPosition].kelompok[mKelompokListPosition].taskList.add(mTaskListPosition, task)

        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        FirestoreClass().addUpdateTopicList(this@CourseTaskDetailActivity,mCourseDetails)


    }

    fun addUpdateTopicListSuccess(){

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

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "KelomproReminderChannel"
            val description = "Channel For Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Kelompro",name,importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm() {
        createNotificationChannel()

        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = mSelectedDueDateMilliSeconds - currentTimeMillis

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE) // Specify FLAG_IMMUTABLE

        if (timeDifferenceMillis >= 3600000L) { // Check if the time difference is at least 1 hour
            // Set the alarm to trigger 1 hour before the due date
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                mSelectedDueDateMilliSeconds - 3600000L,
                pendingIntent
            )

            Toast.makeText(this, "Alarm set for 1 hour before task due date", Toast.LENGTH_SHORT).show()
        } else {
            // If the time difference is less than 1 hour, cancel any existing alarm
            cancelAlarm()
        }
    }


    private fun cancelAlarm(){

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Alarm Canceled",Toast.LENGTH_LONG).show()
    }

}