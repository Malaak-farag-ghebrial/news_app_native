package com.example.netowrk_training.ui.viewmodel

import android.R
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.netowrk_training.models.Article
import com.example.netowrk_training.models.News
import com.example.netowrk_training.repository.NewsRepository
import com.example.netowrk_training.utils.AppStates
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app: Application, val newsRepo: NewsRepository) : AndroidViewModel(app) {

    val headLines: MutableLiveData<AppStates<News>> = MutableLiveData()
    var headlinesPage: Int = 1
    var headlineResponse: News? = null

    val searchNews: MutableLiveData<AppStates<News>> = MutableLiveData()
    var searchPage: Int = 1
    var searchResponse: News? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    private fun handleHeadlinesResponse(response: Response<News>): AppStates<News> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                headlinesPage++
                if (headlineResponse == null) {
                    headlineResponse = result
                } else {
                    val oldArticle = headlineResponse?.articles.orEmpty()
                    val newArticle = result.articles.orEmpty()
                    val combined = oldArticle + newArticle
                    headlineResponse = headlineResponse?.copy(articles = combined)
                }
                return AppStates.Success(headlineResponse ?: result)

            }

        }
        return AppStates.Error(response.message())
    }

    private fun handleSearchNewsByKeywordResponse(response: Response<News>): AppStates<News> {

        if (response.isSuccessful) {
            response.body()?.let { result ->
                searchPage++
                if (searchResponse == null) {
                    searchResponse = result
                } else {
                    val oldArticle = searchResponse?.articles.orEmpty()
                    val newArticle = result.articles.orEmpty()
                    val combined = oldArticle + newArticle
                    searchResponse = searchResponse?.copy(articles = combined)
                }
                return AppStates.Success(searchResponse ?: result)
            }

        }
        return AppStates.Error(response.message())
    }


    fun addToFavorite(article: Article) = viewModelScope.launch {
        newsRepo.addToFavorite(article)
    }

    fun getFavoriteArticle() = newsRepo.getFavorite()


    fun deleteArticleFromFavorite(article: Article) = viewModelScope.launch {
        newsRepo.removeFromFavorite(article)
    }

    fun checkConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    fun getHeadLines(countryCode: String) = viewModelScope.launch {
        getHeadlinesResponse(countryCode)
    }

    fun searchNews(keyword: String) = viewModelScope.launch {
        searchNewsResponse(keyword)
    }


    private suspend fun getHeadlinesResponse(country: String) {
        headLines.postValue(AppStates.Loading())
        try {
            if (checkConnection(this.getApplication())) {
                val response = newsRepo.getHeadlineNews(country, headlinesPage)
                headLines.postValue(handleHeadlinesResponse(response))
            } else {
                headLines.postValue(AppStates.Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> headLines.postValue(AppStates.Error("Unable to Connect"))
                else -> headLines.postValue(AppStates.Error("No signal"))

            }


        }


    }

    private suspend fun searchNewsResponse(query: String) {
        searchNews.postValue(AppStates.Loading())
        try {
            if(query != oldSearchQuery){
                searchPage = 1
                searchResponse = null
            }


            if (checkConnection(this.getApplication())) {
                val response = newsRepo.searchNews(query, searchPage)
                searchNews.postValue(handleSearchNewsByKeywordResponse(response))
                oldSearchQuery = query
            } else {
                searchNews.postValue(AppStates.Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(AppStates.Error("Unable to Connect"))
                else -> searchNews.postValue(AppStates.Error("No signal"))

            }


        }


    }

}