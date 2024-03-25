package com.example.kelomproapp.ui.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityMainGuruBinding

class MainGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main_guru)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_kelompok, R.id.navigation_siswa, R.id.navigation_guru,R.id.navigation_pengaturan
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}