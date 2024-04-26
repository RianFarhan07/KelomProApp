package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    val name: String = "",
    var topicList : ArrayList<Topic> = ArrayList(),
    val guru: String = "",
    val classes : String = "",
    var documentId : String = ""
) : Parcelable