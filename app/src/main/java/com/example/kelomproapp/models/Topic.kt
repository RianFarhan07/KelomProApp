package com.example.kelomproapp.models

data class Topic (
        val name: String = "",
        var kelompok : ArrayList<Kelompok> = ArrayList(),
        var documentId : String = ""
        )