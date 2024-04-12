package com.company.miniproject1.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.company.miniproject1.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Sign_Up_Activity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        // For going back if already a user
        binding?.tvLoginPage?.setOnClickListener{
            startActivity(Intent(this, Log_in_activity::class.java))
            finish()
        }

        // For new user sign up
        binding?.btnSignUp?.setOnClickListener{
            userSignUp()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Log_in_activity::class.java))
        finish()
    }

    private fun userSignUp() {
        var name = binding?.etSinUpName?.text.toString()
        var email = binding?.etSinUpEmail?.text.toString()
        var password = binding?.etSinUpPassword?.text.toString()
        if (validateForm(name, email, password)){
            showProgressBar()
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    hideProgressBar()
                }
        }
    }

    private fun validateForm(name:String, email:String,password:String):Boolean
    {
        return when {
            TextUtils.isEmpty(name)->{
                binding?.tilName?.error = "Enter name"
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()->{
                binding?.tilEmail?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.tilPassword?.error = "Enter password"
                false
            }
            else -> { true }
        }
    }
}