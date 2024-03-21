package com.example.kelomproapp.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectedAnggota (
    val id: String = "",
    val image: String = ""
        ) : Parcelable