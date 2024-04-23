package com.example.kelomproapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityCourseListBinding

class CourseListActivity : AppCompatActivity() {
    private var binding : ActivityCourseListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCourseListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
    }


}