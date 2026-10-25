package com.example.booknest

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)
        applySystemInsets()

        val emailEt = findViewById<EditText>(R.id.loginEmailEditText)
        val passEt = findViewById<EditText>(R.id.loginPasswordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val createTv = findViewById<TextView>(R.id.createAccountTextView)

        loginBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passEt.text.toString()

            when {
                email.isEmpty() -> emailEt.error = "Email is required"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> emailEt.error = "Invalid email format"
                password.isEmpty() -> passEt.error = "Password is required"
                else -> {
                    loginBtn.isEnabled = false
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { goToMain() }
                        .addOnFailureListener { e ->
                            loginBtn.isEnabled = true
                            val msg = when (e) {
                                is FirebaseAuthInvalidUserException,
                                is FirebaseAuthInvalidCredentialsException -> "Wrong email or password"
                                else -> e.message ?: "Login failed"
                            }
                            toast(msg)
                        }
                }
            }
        }

        createTv.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}