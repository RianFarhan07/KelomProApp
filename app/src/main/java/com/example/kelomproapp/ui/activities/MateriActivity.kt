package com.example.kelomproapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityMateriBinding

class MateriActivity : AppCompatActivity() {
    private var binding : ActivityMateriBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMateriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
    }


}