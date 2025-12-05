package com.example.netowrk_training.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import androidx.fragment.app.Fragment
import com.example.netowrk_training.R
import com.example.netowrk_training.databinding.FragmentArticleBinding
import com.example.netowrk_training.ui.MainActivity
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel : NewsViewModel
    val args : ArticleFragmentArgs by navArgs<ArticleFragmentArgs>()
    lateinit var binding: FragmentArticleBinding



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as MainActivity).newsViewModel
        val article = args.article

        binding.webviewArticle.apply {
            webViewClient = WebViewClient()
            article.url.let {
                loadUrl(it)
            }
        }

        binding.floatingFavButton.setOnClickListener {
            newsViewModel.addToFavorite(article)
            Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
        }

    }

}