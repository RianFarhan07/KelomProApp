package com.example.kelomproapp.models

data class Course(
    val name: String = "",
    var topicList : ArrayList<Topic> = ArrayList(),
    val guru: String = "",
    val classes : String = "",
    var documentId : String = ""
)