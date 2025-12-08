package com.example.netowrk_training.models

data class News(
    val articles: List<Article>? = emptyList(),
    val status: String?=null,
    val totalResults: Int = 0
)