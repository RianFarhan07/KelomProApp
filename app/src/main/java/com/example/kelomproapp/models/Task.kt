package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Task (
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val dueDate: Long = 0,
    val pdfUrl: String = "",
    val nilai: String = "",
    val kelompokName: String = "",
    val kelompokCourse: String = "",
    val kelompokTopic: String = "",
    val taskDocumentId: String = UUID.randomUUID().toString()
) : Parcelable{

    fun isCompleted(): Boolean {
        return pdfUrl.isNotEmpty()
    }
}