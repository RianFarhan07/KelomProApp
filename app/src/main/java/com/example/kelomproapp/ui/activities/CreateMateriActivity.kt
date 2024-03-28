package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityCreateMateriBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class CreateMateriActivity : BaseActivity() {
    private var binding : ActivityCreateMateriBinding? = null
    private var selectedFileUri: Uri? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateMateriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()

        storageReference = FirebaseStorage.getInstance().reference

        binding?.btnUploadPdf?.setOnClickListener {
            // Launch file picker intent
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, Constants.PICK_FILE_REQUEST_CODE)
        }

        binding?.btnCreate?.setOnClickListener {
            // Check if a file is selected
            selectedFileUri?.let { uri ->
                uploadFileToFirebase(uri)
            } ?: Toast.makeText(this,"Please Select File To Upload",Toast.LENGTH_LONG).show()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCreateMateriActivity)
        val toolbar = supportActionBar
        if (toolbar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarCreateMateriActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Get selected file URI
            selectedFileUri = data.data
            binding?.textViewUploadedPdfName?.text = selectedFileUri?.lastPathSegment
        }
    }

    private fun uploadFileToFirebase(fileUri: Uri) {
        showProgressDialog("Sedang Mengupload File")

        // Generate a random file name for the uploaded file
        val fileName = UUID.randomUUID().toString()

        // Reference to the file location in Firebase Storage
        val fileRef = storageReference!!.child(Constants.MATERI).child(fileName)

        // Upload file to Firebase Storage
        fileRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                hideProgressDialog()
                Toast.makeText(this,"Upload Success",Toast.LENGTH_LONG).show()

                // Get the download URL for the uploaded file
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val downloadUrl = downloadUri.toString()
                    // Create Materi object with the download URL
                    val materi = Materi(
                        name = binding?.etMateriName?.text.toString(),
                        courses = binding?.etCourse?.text.toString(),
                        topic = binding?.etTopic?.text.toString(),
                        url = downloadUrl
                    )

                    // Save the Materi object to Firestore or perform other operations as needed
                    // FirestoreClass().createMateri(materi)
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                Toast.makeText(this,"Failed to upload file: ${exception.message}",Toast.LENGTH_LONG).show()
            }
    }

    private fun createMateri(){

        var materi = Materi(
            binding?.etMateriName?.text.toString(),
            binding?.etCourse?.text.toString(),
            binding?.etTopic?.text.toString(),
//            assignedUserArrayList,
        )

//        FirestoreClass().createKelompok(this,kelompok)
    }

    fun kelompokCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }




}