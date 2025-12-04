package com.example.netowrk_training.repository

import com.example.netowrk_training.database.ArticleDatabase
import com.example.netowrk_training.models.Article
import com.example.netowrk_training.remote.RetrofitInstance

class NewsRepository(val database: ArticleDatabase) {

    suspend fun getHeadlineNews(country: String, page: Int) =
        RetrofitInstance.api.getHeadlines(country, page)

    suspend fun searchNews(keyword: String, page: Int) =
        RetrofitInstance.api.searchNews(keyword, page)

    suspend fun addToFavorite(article: Article) =
        database.articleDAO().insert(article)

    fun getFavorite() = database.articleDAO().getAllArticles()

    suspend fun removeFromFavorite(article: Article) =
        database.articleDAO().deleteArticle(article)

}