package com.example.netowrk_training.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.netowrk_training.repository.NewsRepository

class NewsProviderViewModel(val app: Application,val newsRepo: NewsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepo) as T
    }


}