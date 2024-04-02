package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.databinding.ActivityMainBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener{

    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName : String
    private lateinit var mSiswaId : String

    companion object{
        const val MY_PROFILE_REQUEST_CODE = 11
        const val CREATE_KELOMPOK_REQUEST_CODE = 12
        const val UPDATE_KELOMPOK_REQUEST_CODE = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this)


        FirestoreClass().getUserDetails(this,"siswa",true)

        val fabCreateKelompok : FloatingActionButton = findViewById(R.id.fab_create_kelompok)
        fabCreateKelompok.setOnClickListener {
            val intent = Intent(this, CreateKelompokActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_KELOMPOK_REQUEST_CODE)
        }
    }

    private fun setupActionBar() {
        val toolbar : Toolbar = findViewById(R.id.toolbar_main_activity)

        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener {
            toogleDrawer()
        }

    }

    private fun toogleDrawer(){

        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {

        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)

        } else{
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().getUserDetails(this,Constants.SISWA)
        }else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_KELOMPOK_REQUEST_CODE){
            FirestoreClass().getKelompokList(this)
        }else if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_KELOMPOK_REQUEST_CODE) {
            FirestoreClass().getKelompokList(this)
        }
        else{
            Log.e("cancelled","cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_profile_saya -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_my_task -> {

                val intent = Intent(this, MyTaskActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_materi -> {
                val intent = Intent(this, MateriActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_panduan -> {
                val intent = Intent(this, PanduanActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_delete_akun -> {
                showDialogToDeleteAccount()
            }

        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)

        return true
    }

    fun updateNavigationUserDetails(siswa : Siswa, readKelompokList: Boolean){
        mUserName = "${siswa.firstName} ${siswa.lastName}"
        mSiswaId = siswa.id

        Glide
            .with(this)
            .load(siswa.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_user_image))

        var tvUserName : TextView = findViewById(R.id.tv_username)
        tvUserName.text = "${siswa.firstName} ${siswa.lastName}"

        if (readKelompokList){
            showProgressDialog(resources.getString(R.string.mohon_tunggu))

            FirestoreClass().getKelompokList(this)
        }
    }

    fun populateKelompokListToUI(kelompokList: ArrayList<Kelompok>){

        val rvKelompokList : RecyclerView = findViewById(R.id.rv_kelompok_list)
        val tvNoKelompokAvailable : TextView = findViewById(R.id.tv_no_kelompok_available)

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
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivityForResult(intent, UPDATE_KELOMPOK_REQUEST_CODE)
                }
            })
        }else{
            rvKelompokList.visibility = View.GONE
            tvNoKelompokAvailable.visibility  = View.VISIBLE
        }
    }

    private fun showDialogToDeleteAccount() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
        builder.setMessage("Apakah Anda yakin ingin menghapus akun?")

        builder.setPositiveButton("Ya") { dialog, which ->
            deleteAkunSiswa()
        }

        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAkunSiswa() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnSuccessListener {
                FirestoreClass().deleteSiswa(this@MainActivity, mSiswaId)

                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete account: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    fun deleteSiswaSuccess(){
        Toast.makeText(this,"Berhasil menghapus akun siswa",
            Toast.LENGTH_LONG).show()
    }
}