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

class AnggotaActivity : BaseActivity() {
    private var binding : ActivityAnggotaBinding? = null
    private lateinit var mKelompokDetails : Kelompok
    private lateinit var mAssignedAnggotaList : ArrayList<Siswa>
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnggotaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.KELOMPOK_DETAIL)){
            mKelompokDetails = intent.getParcelableExtra<Kelompok>(Constants.KELOMPOK_DETAIL)!!
        }

        setupActionBar()
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        FirestoreClass().getAssignedAnggotaListDetails(this,mKelompokDetails.assignedTo)


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

    fun setupAnggotaList(list: ArrayList<Siswa>){
        hideProgressDialog()
        mAssignedAnggotaList = list

        binding?.rvAnggotaList?.layoutManager = LinearLayoutManager(this)
        binding?.rvAnggotaList?.setHasFixedSize(true)

        val adapter = SiswaItemsAdapter(this,list)

        binding?.rvAnggotaList?.adapter = adapter


    }

    fun anggotaDetails(siswa: Siswa){
        mKelompokDetails.assignedTo.add(siswa.id)
        FirestoreClass().assignedAnggotaToKelompok(this,mKelompokDetails,siswa)
    }

    fun anggotaAssignedSuccess(siswa: Siswa){
        hideProgressDialog()
        mAssignedAnggotaList.add(siswa)
        setupAnggotaList(mAssignedAnggotaList)
        anyChangesMade = true

        SendNotificationToUserAsyncTask(mKelompokDetails.name!!,siswa.fcmToken!!).execute()
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