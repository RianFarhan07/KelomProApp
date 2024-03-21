package com.example.kelomproapp.ui.activities

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private var binding : ActivityForgotPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val typeFaceAmorria = Typeface.createFromAsset(assets,"Amorria-Brush.ttf")
        binding?.tvAppName?.typeface = typeFaceAmorria

        setupActionBar()

        binding?.btnSubmit?.setOnClickListener {
            val email: String = binding?.etEmail?.text.toString().trim(){it <= ' '}
            if (email.isEmpty()){
                showErrorSnackBar("Masukkan email anda",true)
            }else{
                showProgressDialog(resources.getString(R.string.mohon_tunggu))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgotPasswordActivity,
                            "email reset password berhasil dikirim ke $email",
                            Toast.LENGTH_LONG).show()
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarForgotPasswordActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding?.toolbarForgotPasswordActivity?.setNavigationOnClickListener { onBackPressed() }
    }
}