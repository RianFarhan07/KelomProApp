package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.kelomproapp.databinding.ActivitySplashBinding
import com.example.kelomproapp.firebase.FirestoreClass

class SplashActivity : BaseActivity() {
    private var binding : ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeFacePixel: Typeface = Typeface.createFromAsset(assets,"PixelArmy.ttf")
        val typeFaceBrazil: Typeface = Typeface.createFromAsset(assets,"BrasiliaDelight-Regular.ttf")

        binding?.tvAppName?.typeface = typeFacePixel
        binding?.tvAppDesc?.typeface = typeFaceBrazil

        binding?.ivIcon?.setOnClickListener{
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        Handler().postDelayed({
            checkUserLoggedIn()
        }, 3000)
    }

    private fun checkUserLoggedIn() {
        val currentUserID = FirestoreClass().getCurrentUserID()

        Handler().postDelayed({
            if (currentUserID.isNotEmpty()) {
                FirestoreClass().getUserRole(currentUserID) { role ->
                    if (role != null) {
                        handleUserRole(role)
                    }
                }
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }, 2000)
    }

    fun handleUserRole(role: String) {
        when(role) {
            "siswa" -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            "guru" -> {
                startActivity(Intent(this, CourseActivity::class.java))
            }
        }
        finish()
    }
}
