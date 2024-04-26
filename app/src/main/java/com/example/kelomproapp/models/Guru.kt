package com.example.kelomproapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Guru(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val role : String = "guru"
) : Parcelable
{
    fun isGuru(): Boolean {
        return true
    }
}
