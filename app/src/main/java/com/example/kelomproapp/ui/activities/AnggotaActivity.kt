package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.SiswaItemsAdapter
import com.example.kelomproapp.databinding.ActivityAnggotaBinding
import com.example.kelomproapp.databinding.DialogSearchAnggotaBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Course
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants
import com.example.kelomproapp.utils.SwipeToDeleteCallback
import com.google.api.Http
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class AnggotaActivity : BaseActivity(), SiswaItemsAdapter.OnDeleteAnggotaClickListener  {
    private var binding : ActivityAnggotaBinding? = null
    private lateinit var mKelompokDetails : Kelompok
    private lateinit var mCourseDetail : Course
    private lateinit var mAssignedAnggotaList : ArrayList<Siswa>
    private var mToCourse : Boolean = false
    private var mTopicListPosition = -1
    private var mKelompokListPosition = -1
    private var anyChangesMade : Boolean = false
    private lateinit var adapter: SiswaItemsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnggotaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.KELOMPOK_DETAIL)){
            mKelompokDetails = intent.getParcelableExtra<Kelompok>(Constants.KELOMPOK_DETAIL)!!
        }

        if (intent.hasExtra(Constants.TO_COURSE)){
            mToCourse = intent.getBooleanExtra(Constants.TO_COURSE,false)
            Log.e("TOCOURSE ", mToCourse.toString())
        }
        if (intent.hasExtra(Constants.TOPIC_LIST_ITEM_POSITION)){
            mTopicListPosition = intent.getIntExtra(Constants.TOPIC_LIST_ITEM_POSITION,-1)
            Log.e("mtopiclistposition ", mTopicListPosition.toString())
        }
        if (intent.hasExtra(Constants.KELOMPOK_LIST_ITEM_POSITION)){
            mKelompokListPosition = intent.getIntExtra(Constants.KELOMPOK_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.COURSE_DETAIL)){
            mCourseDetail = intent.getParcelableExtra(Constants.COURSE_DETAIL)!!
        }


        setupActionBar()
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        if (mToCourse){
            FirestoreClass().getAssignedAnggotaListDetails(this,
                mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].assignedTo)
        }else{
            FirestoreClass().getAssignedAnggotaListDetails(this,mKelompokDetails.assignedTo)
        }



    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarAnggotaActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarAnggotaActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_anggota,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_anggota -> {
                dialogSearchAnggota()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun setupAnggotaList(list: ArrayList<Siswa>) {
        hideProgressDialog()
        mAssignedAnggotaList = list

        binding?.rvAnggotaList?.layoutManager = LinearLayoutManager(this)
        binding?.rvAnggotaList?.setHasFixedSize(true)

        adapter = SiswaItemsAdapter(this, list, true)
        adapter.setOnDeleteAnggotaClickListener(this)

        binding?.rvAnggotaList?.adapter = adapter
    }

    fun anggotaDetails(siswa: Siswa) {
        if (mToCourse) {
            mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].assignedTo.add(siswa.id)
            anggotaAssignedSuccess(siswa)
            FirestoreClass().addUpdateTopicList(this, mCourseDetail)
        } else {
            mKelompokDetails.assignedTo.add(siswa.id)
            anggotaAssignedSuccess(siswa)
            FirestoreClass().assignedAnggotaToKelompok(this, mKelompokDetails, siswa)
        }
    }


    fun anggotaAssignedSuccess(siswa: Siswa){
        hideProgressDialog()
        mAssignedAnggotaList.add(siswa)
        setupAnggotaList(mAssignedAnggotaList)
        anyChangesMade = true

        if (mToCourse){
            SendNotificationToUserAsyncTask(
                mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].name!!,siswa.fcmToken!!).execute()
        }else{
            SendNotificationToUserAsyncTask(mKelompokDetails.name!!,siswa.fcmToken!!).execute()
        }

    }

    private fun dialogSearchAnggota() {
        val binding = DialogSearchAnggotaBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)

        binding.tvAdd.setOnClickListener {
            val email = binding.etEmailSearchAnggota.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.mohon_tunggu))
                FirestoreClass().getAnggotaDetails(this,email)

            } else {
                Toast.makeText(this@AnggotaActivity, "Harap Masukkan Email Anggota",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDeleteAnggotaClick(position: Int) {
        val siswa = mAssignedAnggotaList[position]

        if (siswa.id == FirestoreClass().getCurrentUserID()) {
            Toast.makeText(this, "Anda tidak dapat menghapus diri sendiri dari kelompok", Toast.LENGTH_SHORT).show()
        } else {
            if(mToCourse){
                mCourseDetail.topicList[mTopicListPosition].kelompok[mKelompokListPosition].assignedTo.remove(siswa.id)
                mAssignedAnggotaList.removeAt(position)
                adapter.notifyDataSetChanged()

                FirestoreClass().addUpdateTopicList(this, mCourseDetail)
            }else{
                mKelompokDetails.assignedTo.remove(siswa.id)
                mAssignedAnggotaList.removeAt(position)
                adapter.notifyDataSetChanged()

                FirestoreClass().unassignAnggotaFromKelompok(this, mKelompokDetails, siswa)
            }

        }
    }


    fun anggotaUnassignedSuccess(siswa: Siswa) {
        // Update UI or perform any necessary actions
        Toast.makeText(this, "Anggota ${siswa.firstName} dihapus dari kelompok", Toast.LENGTH_SHORT).show()
        anyChangesMade = true
        mAssignedAnggotaList.remove(siswa)
        adapter.notifyDataSetChanged()
    }


    private inner class SendNotificationToUserAsyncTask(val namaKelompok :String, val token: String)
        : AsyncTask<Any,Void,String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.mohon_tunggu))
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result : String
            var connection : HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod= "POST"

                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("Charset","utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION,"${Constants.FCM_KEY}= ${Constants.FCM_SERVER_KEY}")

                connection.useCaches = false

                val writer = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Ditambahkan Ke Kelompok $namaKelompok")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "Kamu telah ditambahkan ke kelompok baru oleh ${mAssignedAnggotaList[0].firstName}")

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY, token)

                writer.writeBytes(jsonRequest.toString())
                writer.flush()
                writer.close()

                val httpResult : Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(
                        InputStreamReader(inputStream))
                    val sb = java.lang.StringBuilder()
                    var line : String?
                    try {
                        while (reader.readLine().also {line=it} != null){
                            sb.append(line+"\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection TimeOut"
            }catch (e: java.lang.Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
            Log.e("JSON Response Result",result!!)
        }
    }
}