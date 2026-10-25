package com.example.booknest

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

const val MAX_CAPTION = 1024

const val PASSWORD_RULE = "Password must be at least 6 characters and contain 2 special characters (e.g. @ # $ !)"

fun isStrongPassword(p: String): Boolean =
    p.length >= 6 && p.count { !it.isLetterOrDigit() && !it.isWhitespace() } >= 2

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun AppCompatActivity.applySystemInsets() {
    val root = findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
}