package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import com.example.kelomproapp.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIntroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val typeFaceAmorria: Typeface = Typeface.createFromAsset(assets,"Amorria-Brush.ttf")
        binding?.tvAppName?.typeface = typeFaceAmorria

        binding?.btnSignIn?.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignUp?.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }


}