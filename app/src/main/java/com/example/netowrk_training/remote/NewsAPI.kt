package com.example.netowrk_training.remote

import com.example.netowrk_training.models.News
import com.example.netowrk_training.utils.Constants.Companion.API_KEY
import com.example.netowrk_training.utils.Constants.Companion.QUERY_PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        country: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PAGE_SIZE,
        @Query("apiKey")
        apiKey:String = API_KEY
    ) : Response<News>


    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchKeyword: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PAGE_SIZE,
        @Query("apiKey")
        apiKey:String = API_KEY
        ) : Response<News>

}