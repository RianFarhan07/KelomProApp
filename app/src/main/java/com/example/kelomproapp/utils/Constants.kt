package com.example.kelomproapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val SISWA: String = "siswa"
    const val GURU: String = "guru"
    const val KELOMPOK: String = "kelompok"
    const val MATERI: String = "materi"

    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val FIRST_NAME : String = "firstName"
    const val LAST_NAME : String = "lastName"
    const val MOBILE : String = "mobile"
    const val CLASSES : String = "classes"
    const val ASSIGNED_TO : String = "assignedTo"

    const val KELOMPOK_IMAGE : String = "image"
    const val KELOMPOK_NAME : String = "name"
    const val KELOMPOK_CLASSES : String = "classes"
    const val KELOMPOK_COURSES : String = "course"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val PICK_FILE_REQUEST_CODE = 3

    const val DOCUMENT_ID : String = "documentId"
    const val TASK_ID : String = "taskDocumentId"
    const val MATERI_ID : String = "materiId"
    const val TASK_LIST : String = "taskList"
    const val KELOMPOK_DETAIL : String = "kelompok_detail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val LIST_ANGGOTA_KELOMPOK = "list_anggota_kelompok"
    const val SELECT: String = "select"
    const val UN_SELECT: String = "unSelect"

    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"


    fun showImageChooser(activity : Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}