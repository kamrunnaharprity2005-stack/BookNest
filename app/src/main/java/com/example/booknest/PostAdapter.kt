package com.example.booknest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private var posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.VH>() {

    private val fmt = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH)

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val avatar: TextView = v.findViewById(R.id.avatarTextView)
        val username: TextView = v.findViewById(R.id.usernameTextView)
        val date: TextView = v.findViewById(R.id.dateTextView)
        val bookName: TextView = v.findViewById(R.id.bookNameTextView)
        val author: TextView = v.findViewById(R.id.authorTextView)
        val image: ImageView = v.findViewById(R.id.postImageView)
        val caption: TextView = v.findViewById(R.id.captionTextView)
    }

    fun submit(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = posts[position]
        holder.avatar.text = p.username.firstOrNull()?.uppercase() ?: ""
        holder.username.text = p.username
        holder.date.text = fmt.format(Date(p.timestamp))

        // Purano post-e bookName/author na thakle hide
        holder.bookName.visibility = if (p.bookName.isEmpty()) View.GONE else View.VISIBLE
        holder.author.visibility = if (p.author.isEmpty()) View.GONE else View.VISIBLE
        holder.bookName.text = p.bookName
        holder.author.text = "by ${p.author}"

        holder.caption.text = p.caption
        Glide.with(holder.image).load(p.imageUrl).into(holder.image)
    }
}