package com.example.booknest

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val postsRef = db.getReference("posts")

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var searchEt: EditText
    private lateinit var adapter: PostAdapter
    private val allPosts = mutableListOf<Post>()
    private var postsListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = auth.currentUser
        if (user == null) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_main)
        applySystemInsets()

        drawerLayout = findViewById(R.id.drawerLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.navigationView)
        searchEt = findViewById(R.id.searchEditText)
        val recycler = findViewById<RecyclerView>(R.id.feedRecyclerView)

        // Drawer icon: tint bondho kore icon-er nijer rong (kalo) dekhano
        navView.itemIconTintList = null
        // Prottek item-er niche divider (PDF-er moto)
        val menuView = navView.getChildAt(0) as? RecyclerView
        menuView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#E3E5EC")
                strokeWidth = 2f
            }

            override fun onDrawOver(
                c: android.graphics.Canvas,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val startX = parent.paddingLeft + 48f
                val endX = parent.width - parent.paddingRight - 48f
                for (i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    // Shesh item-er niche divider lagbe na
                    if (parent.getChildAdapterPosition(child) == state.itemCount - 1) continue
                    val y = child.bottom.toFloat()
                    c.drawLine(startX, y, endX, y, paint)
                }
            }
        })

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        adapter = PostAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val header = navView.getHeaderView(0)
        val initialTv = header.findViewById<TextView>(R.id.headerInitialTextView)
        val nameTv = header.findViewById<TextView>(R.id.headerUsernameTextView)
        val emailTv = header.findViewById<TextView>(R.id.headerEmailTextView)
        emailTv.text = user.email
        db.getReference("users").child(user.uid).get().addOnSuccessListener { snap ->
            val username = snap.child("username").getValue(String::class.java) ?: ""
            nameTv.text = username
            initialTv.text = username.firstOrNull()?.uppercase() ?: ""
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_create_post -> startActivity(Intent(this, CreatePostActivity::class.java))
                R.id.nav_your_posts -> startActivity(Intent(this, YourPostActivity::class.java))
                R.id.nav_reset_password -> startActivity(Intent(this, ResetPasswordActivity::class.java))
                R.id.nav_logout -> {
                    auth.signOut()
                    goToLogin()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })

        loadPosts()
    }

    private fun loadPosts() {
        postsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allPosts.clear()
                for (child in snapshot.children) {
                    val post = child.getValue(Post::class.java) ?: continue
                    post.id = child.key ?: ""
                    allPosts.add(post)
                }
                allPosts.sortByDescending { it.timestamp }
                applyFilter()
            }

            override fun onCancelled(error: DatabaseError) {
                toast(error.message)
            }
        }
        postsRef.addValueEventListener(postsListener!!)
    }

    private fun applyFilter() {
        val q = searchEt.text.toString().trim().lowercase()
        val list = if (q.isEmpty()) allPosts.toList()
        else allPosts.filter {
            it.bookName.lowercase().contains(q) ||
                    it.author.lowercase().contains(q) ||
                    it.username.lowercase().contains(q)
        }
        adapter.submit(list)
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        postsListener?.let { postsRef.removeEventListener(it) }
    }
}