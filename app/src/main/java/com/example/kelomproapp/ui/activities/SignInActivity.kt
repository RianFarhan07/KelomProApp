package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivitySignInBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity(), View.OnClickListener {

    private var binding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val typeFaceAmorria = Typeface.createFromAsset(assets,"Amorria-Brush.ttf")
        binding?.tvTitle?.typeface = typeFaceAmorria

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding?.btnLogin?.setOnClickListener(this)
        binding?.tvRegister?.setOnClickListener(this)
        binding?.tvForgotPassword?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    loginRegisteredUser()
                }

                R.id.tv_register -> {
                    val intent = Intent(this, SignUpActivity::class.java )
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails() : Boolean {
        return when{
            TextUtils.isEmpty(binding?.etEmail?.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar("Silahkan isi email anda",true)
                false
            }
            TextUtils.isEmpty(binding?.etPassword?.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar("Silahkan isi password anda",true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser(){
        if (validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.mohon_tunggu))

            val email = binding?.etEmail?.text.toString().trim{ it <= ' '}
            val password = binding?.etPassword?.text.toString().trim{it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful){
                        FirestoreClass().getUserDetails(this)
                    }else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user : User){
        hideProgressDialog()
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}