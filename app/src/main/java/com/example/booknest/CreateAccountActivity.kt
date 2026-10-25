package com.example.booknest

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

class CreateAccountActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        applySystemInsets()

        val usernameEt = findViewById<EditText>(R.id.usernameEditText)
        val emailEt = findViewById<EditText>(R.id.emailEditText)
        val passEt = findViewById<EditText>(R.id.passwordEditText)
        val confirmEt = findViewById<EditText>(R.id.confirmPasswordEditText)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val loginTv = findViewById<TextView>(R.id.loginLinkTextView)

        registerBtn.setOnClickListener {
            val username = usernameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passEt.text.toString()
            val confirm = confirmEt.text.toString()

            when {
                !Regex("^[A-Za-z0-9_]{3,20}$").matches(username) ->
                    usernameEt.error = "3-20 chars: letters, numbers, underscore only"
                email.isEmpty() -> emailEt.error = "Email is required"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> emailEt.error = "Invalid email format"
                !isStrongPassword(password) -> passEt.error = PASSWORD_RULE
                password != confirm -> confirmEt.error = "Passwords do not match"
                else -> register(username, email, password, registerBtn)
            }
        }

        loginTv.setOnClickListener { finish() }
    }

    private fun register(username: String, email: String, password: String, btn: Button) {
        btn.isEnabled = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                claimUsername(result.user!!, username, email, btn)
            }
            .addOnFailureListener { e ->
                btn.isEnabled = true
                toast(
                    if (e is FirebaseAuthUserCollisionException) "This email is already used"
                    else e.message ?: "Registration failed"
                )
            }
    }

    private fun claimUsername(user: FirebaseUser, username: String, email: String, btn: Button) {
        val ref = db.getReference("usernames").child(username.lowercase())
        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                if (currentData.value != null) return Transaction.abort()
                currentData.value = user.uid
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    val data = mapOf("username" to username, "email" to email)
                    db.getReference("users").child(user.uid).setValue(data)
                        .addOnSuccessListener {
                            toast("Account created")
                            startActivity(Intent(this@CreateAccountActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            finish()
                        }
                        .addOnFailureListener {
                            btn.isEnabled = true
                            toast("Failed to save user data")
                        }
                } else {
                    user.delete()
                    btn.isEnabled = true
                    toast("Username is already taken")
                }
            }
        })
    }
}