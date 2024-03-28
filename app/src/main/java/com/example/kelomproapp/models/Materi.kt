package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Materi(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val courses: String = "",
    val topic: String = "",
    val url: String = "",

    ) : Parcelable