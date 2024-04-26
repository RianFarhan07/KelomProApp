package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic (
        val name: String = "",
        var kelompok : ArrayList<Kelompok> = ArrayList(),
        var documentId : String = ""
        ) : Parcelable