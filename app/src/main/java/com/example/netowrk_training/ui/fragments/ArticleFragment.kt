package com.example.netowrk_training.ui.fragments

import android.R.attr.src
import android.R.attr.tint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.example.netowrk_training.R
import com.example.netowrk_training.database.ArticleDatabase
import com.example.netowrk_training.databinding.FragmentArticleBinding
import com.example.netowrk_training.repository.NewsRepository
import com.example.netowrk_training.ui.MainActivity
import com.example.netowrk_training.ui.viewmodel.NewsProviderViewModel
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val newsViewModel : NewsViewModel by activityViewModels {
        NewsProviderViewModel(requireActivity().application,NewsRepository(ArticleDatabase.invoke(requireContext())))
    }
    val args : ArticleFragmentArgs by navArgs<ArticleFragmentArgs>()
    lateinit var binding: FragmentArticleBinding
    var isFavorite : Boolean = false




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        val article = args.article

        binding.webviewArticle.apply {
            webViewClient = WebViewClient()
            article.url.let {
                loadUrl(it)
            }
        }
         newsViewModel.getFavoriteArticle().observe(viewLifecycleOwner,{
            articles -> isFavorite = articles.firstOrNull{
               art ->  art.url == article.url
        } != null


             if(isFavorite){
                 binding.floatingFavButton.setImageResource(R.drawable.favorite_solid)
                 val colorInt = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light) // Replace R.color.red with your desired color resource
                 binding.floatingFavButton.imageTintList = ColorStateList.valueOf(colorInt)
             }

        })

        binding.floatingFavButton.setOnClickListener {

            if (isFavorite) {
                newsViewModel.deleteArticleFromFavorite(article)

                binding.floatingFavButton.setImageResource(R.drawable.favorite)
                binding.floatingFavButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.border))

                isFavorite = false
            } else {
                newsViewModel.addToFavorite(article)

                binding.floatingFavButton.setImageResource(R.drawable.favorite_solid)
                binding.floatingFavButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))

                isFavorite = true
            }
        }



//        binding.floatingFavButton.setOnClickListener {
//
//            if (!isFavorite) {
//                newsViewModel.addToFavorite(article)
//                binding.floatingFavButton.setImageResource(R.drawable.favorite_solid)
//                val colorInt = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light) // Replace R.color.red with your desired color resource
//                binding.floatingFavButton.imageTintList = ColorStateList.valueOf(colorInt)
//                isFavorite = true
//            } else {
//                newsViewModel.deleteArticleFromFavorite(article)
//                    binding.floatingFavButton.setImageResource(R.drawable.favorite)
//                    val colorInt = ContextCompat.getColor(requireContext(), R.color.border)
//                    binding.floatingFavButton.imageTintList = ColorStateList.valueOf(colorInt)
//                isFavorite = false
//            }
//        }


    }



}