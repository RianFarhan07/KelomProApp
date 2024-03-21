package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kelompok (
    val name: String? = "",
    val image: String? = "",
    val createdBy: String? = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val course: String? = "",
    val classes: String? = "",
    val topic: String? = "",
    var documentId: String? = "",
    var taskList : ArrayList<Task> = ArrayList()
        ) : Parcelable