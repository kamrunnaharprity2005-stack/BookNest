package com.example.booknest

data class Post(
    var id: String = "",
    var uid: String = "",
    var username: String = "",
    var bookName: String = "",
    var author: String = "",
    var caption: String = "",
    var imageUrl: String = "",
    var publicId: String = "",
    var timestamp: Long = 0L
)