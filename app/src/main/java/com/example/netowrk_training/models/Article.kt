package com.example.netowrk_training.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity("articles")
data class Article(
    @PrimaryKey(true)
    val id: Int? = null,
    val author: String?=null,
    val content: String?=null,
    val description: String,
    val publishedAt: String,
    val source: Source?=null,
    val title: String,
    val url: String,
    val urlToImage: String? = null
) : Serializable