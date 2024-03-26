package com.example.kelomproapp.models

data class Guru(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val role : String = "guru"
) {
    fun isGuru(): Boolean {
        return true
    }
}
