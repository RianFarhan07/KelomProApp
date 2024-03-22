package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Siswa(
        val id: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val classes: String = "",
        val image: String = "",
        val mobile: String = "",
        var selected: Boolean = false,
        val role : String = "siswa"
) : Parcelable