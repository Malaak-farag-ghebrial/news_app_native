package com.example.netowrk_training.ui.viewmodel

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

class NewsViewModel(app: Application,val newsRepo: NewsRepository) : AndroidViewModel(app) {

    val headLines : MutableLiveData<AppStates<News>> = MutableLiveData()
    var headlinesPage: Int = 1
    var headlineResponse: News? = null

     val searchNews : MutableLiveData<AppStates<News>> = MutableLiveData()
    var searchPage: Int = 1
    var searchResponse : News? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    private fun getHeadlinesResponse(response: Response<News>) : AppStates<News> {
        if(response.isSuccessful){
            response.body()?.let {
                result ->
                headlinesPage++
                if(headlineResponse == null){
                    headlineResponse = result
                }else{
                    val oldArticle = headlineResponse?.articles ?: emptyList()
                    val newArticle = result.articles
                    val combined = oldArticle + newArticle
                    headlineResponse = headlineResponse?.copy(articles = combined)
                }
                return AppStates.Success(headlineResponse ?: result)

            }

        }
        return AppStates.Error(response.message())
    }

    private fun searchNewsByKeywordResponse(response: Response<News>) : AppStates<News> {
        if(response.isSuccessful){
            response.body()?.let {
                    result ->
                searchPage++
                if(searchResponse == null){
                    searchResponse = result
                }else{
                    val oldArticle = searchResponse?.articles ?: emptyList()
                    val newArticle = result.articles
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

    fun getFavoriteArticle() = viewModelScope.launch {
        newsRepo.getFavorite()
    }

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


    private suspend fun getHeadlines(country: String){
        headLines.postValue(AppStates.Loading())
        try{
            if(checkConnection(this.getApplication())){
                val response = newsRepo.getHeadlineNews(country,headlinesPage)
                headLines.postValue(getHeadlinesResponse(response))
            }else{
                headLines.postValue(AppStates.Error("No internet Connection"))
            }
        }catch (t: Throwable){
            when(t) {
                is IOException -> headLines.postValue(AppStates.Error("Unable to Connect"))
                else -> headLines.postValue(AppStates.Error("No signal"))

            }



        }


    }


    private suspend fun searchNews(country: String){
        searchNews.postValue(AppStates.Loading())
        try{
            if(checkConnection(this.getApplication())){
                val response = newsRepo.searchNews(country,searchPage)
                searchNews.postValue(searchNewsByKeywordResponse(response))
            }else{
                searchNews.postValue(AppStates.Error("No internet Connection"))
            }
        }catch (t: Throwable){
            when(t) {
                is IOException -> searchNews.postValue(AppStates.Error("Unable to Connect"))
                else -> searchNews.postValue(AppStates.Error("No signal"))

            }



        }


    }

}