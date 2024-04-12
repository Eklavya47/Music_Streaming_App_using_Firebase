package com.company.miniproject1.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.company.miniproject1.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class Forget_Password_Activity : BaseActivity() {
    private var binding: ActivityForgetPasswordBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // For Submit button
        binding?.btnForgotPasswordSubmit?.setOnClickListener{
            resetPassword()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun resetPassword() {
        var email = binding?.etForgotPasswordEmail?.text.toString()
        if (validateForm(email)){
            showProgressBar()
            auth.sendPasswordResetEmail(email).addOnCompleteListener{task ->
                if (task.isSuccessful){
                    binding?.tilEmailForgetPassword?.visibility = View.GONE
                    binding?.tvSubmitMsg?.visibility = View.VISIBLE
                    binding?.btnForgotPasswordSubmit?.visibility = View.GONE
                }
                else{
                    Toast.makeText(this,"Reset Password Failed. Try again later",Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                    binding?.tilEmailForgetPassword?.visibility = View.VISIBLE
                    binding?.tvSubmitMsg?.visibility = View.GONE
                    binding?.btnForgotPasswordSubmit?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateForm(email: String): Boolean {
        return when{
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding?.tilEmailForgetPassword?.error = "Enter valid email address"
                false
            }
            else -> {true}
        }
    }
}