package com.company.miniproject1.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.company.miniproject1.R
import com.company.miniproject1.databinding.ActivityLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class Log_in_activity : BaseActivity() {
    private var binding: ActivityLogInBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        // For forget password
        binding?.tvForgotPassword?.setOnClickListener{
            startActivity(Intent(this, Forget_Password_Activity::class.java))
        }

        // For Sign up new user
        binding?.tvRegister?.setOnClickListener{
            startActivity(Intent(this, Sign_Up_Activity::class.java))
            finish()
        }

        // For email, password login
        binding?.btnSignIn?.setOnClickListener{
            userlogin()
        }

        // For login with google
        binding?.btnSignInWithGoogle?.setOnClickListener{
            googleLogIn()
        }
    }

    private fun googleLogIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if (result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this,"Sign In Failed. Try Again Later.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{task ->
            if (task.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this,"Login Attempt Failed. Try Again",Toast.LENGTH_SHORT).show()
            }
            hideProgressBar()
        }
    }

    private fun userlogin() {
        var email = binding?.etSinInEmail?.text.toString()
        var password = binding?.etSinInPassword?.text.toString()
        if (validateform(email, password)){
            showProgressBar()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
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

    private fun validateform(email: String, password: String): Boolean {
        return when{
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding?.tilEmail?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding?.tilPassword?.error = "Enter password"
                binding?.tilEmail?.error = null
                false
            }
            else -> {
                binding?.tilEmail?.error = null
                binding?.tilPassword?.error = null
                true
            }
        }
    }
}