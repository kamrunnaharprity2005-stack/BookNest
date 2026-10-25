package com.example.booknest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        applySystemInsets()

        val oldEt = findViewById<EditText>(R.id.oldPasswordEditText)
        val newEt = findViewById<EditText>(R.id.newPasswordEditText)
        val confirmEt = findViewById<EditText>(R.id.confirmNewPasswordEditText)
        val btn = findViewById<Button>(R.id.resetPasswordButton)

        btn.setOnClickListener {
            val old = oldEt.text.toString()
            val new = newEt.text.toString()
            val confirm = confirmEt.text.toString()

            when {
                old.isEmpty() -> oldEt.error = "Old password is required"
                !isStrongPassword(new) -> newEt.error = PASSWORD_RULE
                new != confirm -> confirmEt.error = "Passwords do not match"
                else -> updatePassword(old, new, btn)
            }
        }
    }

    private fun updatePassword(old: String, new: String, btn: Button) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email == null) return

        btn.isEnabled = false
        user.reauthenticate(EmailAuthProvider.getCredential(email, old))
            .addOnSuccessListener {
                user.updatePassword(new)
                    .addOnSuccessListener {
                        toast("Password updated")
                        finish()
                    }
                    .addOnFailureListener {
                        btn.isEnabled = true
                        toast(it.message ?: "Update failed")
                    }
            }
            .addOnFailureListener {
                btn.isEnabled = true
                toast("Old password is wrong")
            }
    }
}