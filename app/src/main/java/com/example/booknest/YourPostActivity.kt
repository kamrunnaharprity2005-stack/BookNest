package com.example.booknest

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class YourPostActivity : AppCompatActivity() {

    private val postsRef = FirebaseDatabase.getInstance().getReference("posts")
    private lateinit var adapter: YourPostAdapter
    private var query: Query? = null
    private var listener: ValueEventListener? = null
    private var pendingPost: Post? = null

    private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val post = pendingPost
        if (uri != null && post != null) changeImage(post, uri)
        pendingPost = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_post)
        applySystemInsets()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            finish()
            return
        }

        adapter = YourPostAdapter(
            emptyList(),
            onChangeImage = { post ->
                pendingPost = post
                picker.launch("image/*")
            },
            onSave = { post, caption -> saveCaption(post, caption) },
            onDelete = { post -> confirmDelete(post) }
        )

        val recycler = findViewById<RecyclerView>(R.id.yourPostsRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        query = postsRef.orderByChild("uid").equalTo(uid)
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    val p = child.getValue(Post::class.java) ?: continue
                    p.id = child.key ?: ""
                    list.add(p)
                }
                list.sortByDescending { it.timestamp }
                adapter.submit(list)
            }

            override fun onCancelled(error: DatabaseError) {
                toast(error.message)
            }
        }
        query!!.addValueEventListener(listener!!)
    }

    private fun saveCaption(post: Post, caption: String) {
        val c = caption.trim()
        when {
            c.isEmpty() -> toast("Caption is required")
            c.length > MAX_CAPTION -> toast("Max $MAX_CAPTION characters")
            else -> postsRef.child(post.id).child("caption").setValue(c)
                .addOnSuccessListener { toast("Caption updated") }
                .addOnFailureListener { toast("Update failed") }
        }
    }

    private fun changeImage(post: Post, uri: Uri) {
        toast("Uploading image...")
        CloudinaryHelper.upload(this, uri,
            onSuccess = { url, publicId ->
                val oldPublicId = post.publicId
                postsRef.child(post.id).updateChildren(
                    mapOf("imageUrl" to url, "publicId" to publicId)
                ).addOnSuccessListener {
                    CloudinaryHelper.delete(oldPublicId) {}
                    toast("Image updated")
                }.addOnFailureListener { toast("Update failed") }
            },
            onError = { toast(it) })
    }

    private fun confirmDelete(post: Post) {
        AlertDialog.Builder(this)
            .setTitle("Delete post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                postsRef.child(post.id).removeValue()
                    .addOnSuccessListener {
                        CloudinaryHelper.delete(post.publicId) { ok ->
                            toast(if (ok) "Post deleted" else "Post deleted (image removal failed)")
                        }
                    }
                    .addOnFailureListener { toast("Delete failed") }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        val q = query
        val l = listener
        if (q != null && l != null) q.removeEventListener(l)
    }
}