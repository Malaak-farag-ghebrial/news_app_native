package com.example.netowrk_training.repository

import android.util.Log
import com.example.netowrk_training.R
import com.example.netowrk_training.database.ArticleDatabase
import com.example.netowrk_training.models.Article
import com.example.netowrk_training.models.News
import com.example.netowrk_training.remote.RetrofitInstance
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class NewsRepository(val database: ArticleDatabase) {




    suspend fun getHeadlineNews(country: String, page: Int) : Response<News> {

        val apiKey = fetchApiKeyFromRemoteConfig()
        return  RetrofitInstance.api.getHeadlines(country, page, apiKey = apiKey)
    }


    suspend fun searchNews(keyword: String, page: Int) : Response<News>
        {

        val apiKey = fetchApiKeyFromRemoteConfig()
           return RetrofitInstance.api.searchNews(keyword, page, apiKey = apiKey)
    }

    suspend fun addToFavorite(article: Article) =
        database.articleDAO().insert(article)

    fun getFavorite() = database.articleDAO().getAllArticles()

    suspend fun removeFromFavorite(article: Article) =
        database.articleDAO().deleteArticle(article)

    private suspend fun fetchApiKeyFromRemoteConfig(): String {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        try {
            val activated = remoteConfig.fetchAndActivate().await()

            if (activated) {
                val newApiKey = remoteConfig.getString("news_api_key")
                Log.d("RemoteConfig", "API Key successfully fetched and activated: $newApiKey")
                return newApiKey
            } else {
                val cachedApiKey = remoteConfig.getString("news_api_key")
                Log.d("RemoteConfig", "Fetch successful but not activated. Using cached key: $cachedApiKey")
                return cachedApiKey
            }
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Fetch failed. Using default key.", e)
            return remoteConfig.getString("news_api_key")
        }
    }

}