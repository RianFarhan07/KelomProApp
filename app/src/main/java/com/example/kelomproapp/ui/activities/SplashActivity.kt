package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.kelomproapp.databinding.ActivitySplashBinding
import com.example.kelomproapp.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    private var binding : ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
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

            var currentUserID = FirestoreClass().getCurrentUserID()

            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        },3000)


    }
}