package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityMainGuruBinding
import com.google.firebase.auth.FirebaseAuth

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
                R.id.navigation_kelompok, R.id.navigation_siswa, R.id.navigation_guru,R.id.navigation_materi
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_kelompok_guru, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val  id = item.itemId

        when(id){
            R.id.action_logOut -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}