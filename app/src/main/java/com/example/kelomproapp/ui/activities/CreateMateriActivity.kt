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
            mSelectedFileUri?.let { uri ->
                uploadFileToFirebase(uri)
            } ?: Toast.makeText(this, "Please Select File To Upload", Toast.LENGTH_LONG).show()
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

        // Generate a random file name for the uploaded file
        val fileName = UUID.randomUUID().toString()

        // Reference to the file location in Firebase Storage
        val fileRef = storageReference!!.child(Constants.MATERI).child(fileName)

        // Upload file to Firebase Storage
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

                    // Save the Materi object to Firestore
                    createMateri(materi)
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

    fun materiCreatedSuccessfully() {
        hideProgressDialog()
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
}
