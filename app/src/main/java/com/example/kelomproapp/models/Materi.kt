package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Materi(
    var id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val courses: String = "",
    val classes: String = "",
    val topic: String = "",
    val url: String = "",
    val fileType: String = ""

    ) : Parcelable