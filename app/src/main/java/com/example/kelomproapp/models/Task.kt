package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task (
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val dueDate: Long = 0,
    val pdfUrl: String = "",
    val nilai: String = ""
        ) : Parcelable