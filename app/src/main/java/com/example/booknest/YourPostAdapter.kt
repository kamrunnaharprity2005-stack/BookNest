package com.example.booknest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class YourPostAdapter(
    private var posts: List<Post>,
    private val onChangeImage: (Post) -> Unit,
    private val onSave: (Post, String) -> Unit,
    private val onDelete: (Post) -> Unit
) : RecyclerView.Adapter<YourPostAdapter.VH>() {

    private val fmt = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH)

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val avatar: TextView = v.findViewById(R.id.avatarTextView)
        val username: TextView = v.findViewById(R.id.usernameTextView)
        val date: TextView = v.findViewById(R.id.dateTextView)
        val image: ImageView = v.findViewById(R.id.postImageView)
        val caption: EditText = v.findViewById(R.id.captionEditText)
        val changeBtn: Button = v.findViewById(R.id.changeImageButton)
        val saveBtn: Button = v.findViewById(R.id.saveButton)
        val deleteBtn: Button = v.findViewById(R.id.deleteButton)
    }

    fun submit(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_your_post, parent, false))

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = posts[position]
        holder.avatar.text = p.username.firstOrNull()?.uppercase() ?: ""
        holder.username.text = p.username
        holder.date.text = fmt.format(Date(p.timestamp))
        holder.caption.setText(p.caption)
        Glide.with(holder.image).load(p.imageUrl).into(holder.image)

        holder.changeBtn.setOnClickListener { onChangeImage(p) }
        holder.saveBtn.setOnClickListener { onSave(p, holder.caption.text.toString()) }
        holder.deleteBtn.setOnClickListener { onDelete(p) }
    }
}