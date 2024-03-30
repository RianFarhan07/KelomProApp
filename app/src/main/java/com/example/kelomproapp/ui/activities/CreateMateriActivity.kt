package com.example.kelomproapp.ui.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityCreateMateriBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class CreateMateriActivity : BaseActivity() {
    private var binding: ActivityCreateMateriBinding? = null
    private var mSelectedFileUri: Uri? = null
    private var mFileType: String? = ""
    private var storageReference: StorageReference? = null
    private var materiId: String? = null
    private var isUpdatingMateri: Boolean = false

    companion object {
        private const val EDIT_MATERI_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateMateriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()

        if (intent.hasExtra(Constants.MATERI_ID)){
            materiId = intent.getStringExtra(Constants.MATERI_ID)
            showProgressDialog("Loading Materi...")
            FirestoreClass().getMateriDetails(this, materiId!!)
            binding?.btnCreate?.text = "UPDATE"
            isUpdatingMateri = true
        }

        storageReference = FirebaseStorage.getInstance().reference

        binding?.btnUploadPdf?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, Constants.PICK_FILE_REQUEST_CODE)
        }

        binding?.btnCreate?.setOnClickListener {
            if (mSelectedFileUri != null) {
                uploadFileToFirebase(mSelectedFileUri!!)
            } else {
                if (isUpdatingMateri){
                    Toast.makeText(this, "Tolong upload lagi file materi", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "Tolong upload file materi", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCreateMateriActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarCreateMateriActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Get selected file URI
            mSelectedFileUri = data.data
            mFileType = getFileType(mSelectedFileUri)
            binding?.textViewUploadedPdfName?.text = mSelectedFileUri?.lastPathSegment
        }
    }

    private fun uploadFileToFirebase(fileUri: Uri) {
        showProgressDialog("Uploading File...")

        val fileName = UUID.randomUUID().toString()
        val fileRef = storageReference!!.child(Constants.MATERI).child(fileName)

        fileRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                hideProgressDialog()
                Toast.makeText(this, "Upload Success", Toast.LENGTH_LONG).show()

                // Get the download URL for the uploaded file
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val downloadUrl = downloadUri.toString()
                    // Create Materi object with the download URL
                    val materi = Materi(
                        name = binding?.etMateriName?.text.toString(),
                        courses = binding?.etCourse?.text.toString(),
                        topic = binding?.etTopic?.text.toString(),
                        url = downloadUrl,
                        fileType = mFileType.toString()
                    )

                    // Save or update the Materi object to Firestore
                    if (isUpdatingMateri) {
                        updateMateri(materiId!!, materi)
                    } else {
                        createMateri(materi)
                    }
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                Toast.makeText(this, "Failed to upload file: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun createMateri(materi: Materi) {
        FirestoreClass().createMateri(this, materi)
    }

    private fun updateMateri(materiId: String, materi: Materi) {
        FirestoreClass().updateMateri(this, materiId, materi)
    }

    fun materiCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun materiUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(this, "Materi updated successfully", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun getFileType(uri: Uri?): String? {
        return if (uri == null) {
            null
        } else {
            val contentResolver: ContentResolver = this.contentResolver
            val mimeTypeMap = MimeTypeMap.getSingleton()
            mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
        }
    }

    fun showMateriDetails(materi: Materi) {
        binding?.apply {
            etMateriName.setText(materi.name)
            etCourse.setText(materi.courses)
            etTopic.setText(materi.topic)
            textViewUploadedPdfName.text = materi.url
            hideProgressDialog()
        }
    }


}
