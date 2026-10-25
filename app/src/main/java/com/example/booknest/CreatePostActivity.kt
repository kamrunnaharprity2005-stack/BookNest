package com.example.booknest

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class CreatePostActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private var selectedUri: Uri? = null
    private lateinit var previewIv: ImageView

    private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedUri = uri
            previewIv.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        applySystemInsets()

        previewIv = findViewById(R.id.postImageView)
        val selectBtn = findViewById<Button>(R.id.selectImageButton)
        val bookEt = findViewById<EditText>(R.id.bookNameEditText)
        val authorEt = findViewById<EditText>(R.id.authorEditText)
        val captionEt = findViewById<EditText>(R.id.captionEditText)
        val counterTv = findViewById<TextView>(R.id.characterCountTextView)
        val postBtn = findViewById<Button>(R.id.uploadPostButton)

        selectBtn.setOnClickListener { picker.launch("image/*") }
        previewIv.setOnClickListener { picker.launch("image/*") }

        captionEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                counterTv.text = "${s?.length ?: 0} / $MAX_CAPTION"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        postBtn.setOnClickListener {
            val bookName = bookEt.text.toString().trim()
            val author = authorEt.text.toString().trim()
            val caption = captionEt.text.toString().trim()
            val uri = selectedUri
            when {
                uri == null -> toast("Please select an image")
                bookName.isEmpty() -> bookEt.error = "Book name is required"
                author.isEmpty() -> authorEt.error = "Author name is required"
                caption.isEmpty() -> captionEt.error = "Caption is required"
                caption.length > MAX_CAPTION -> captionEt.error = "Max $MAX_CAPTION characters"
                else -> createPost(uri, bookName, author, caption, postBtn)
            }
        }
    }

    private fun createPost(uri: Uri, bookName: String, author: String, caption: String, btn: Button) {
        val uid = auth.currentUser?.uid ?: return
        btn.isEnabled = false
        btn.text = "Uploading..."

        db.getReference("users").child(uid).child("username").get()
            .addOnSuccessListener { snap ->
                val username = snap.getValue(String::class.java) ?: ""

                CloudinaryHelper.upload(this, uri,
                    onSuccess = { url, publicId ->
                        val postsRef = db.getReference("posts")
                        val id = postsRef.push().key!!
                        val data = mapOf(
                            "uid" to uid,
                            "username" to username,
                            "bookName" to bookName,
                            "author" to author,
                            "caption" to caption,
                            "imageUrl" to url,
                            "publicId" to publicId,
                            "timestamp" to ServerValue.TIMESTAMP
                        )
                        postsRef.child(id).setValue(data)
                            .addOnSuccessListener {
                                toast("Post created")
                                finish()
                            }
                            .addOnFailureListener {
                                resetButton(btn)
                                toast("Failed to save post")
                            }
                    },
                    onError = { msg ->
                        resetButton(btn)
                        toast(msg)
                    })
            }
            .addOnFailureListener {
                resetButton(btn)
                toast("Failed to load user")
            }
    }

    private fun resetButton(btn: Button) {
        btn.isEnabled = true
        btn.text = "Post"
    }
}