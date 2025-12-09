package com.example.netowrk_training.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netowrk_training.R
import com.example.netowrk_training.adapters.NewsListAdapter
import com.example.netowrk_training.database.ArticleDatabase
import com.example.netowrk_training.databinding.FragmentHomeBinding
import com.example.netowrk_training.remote.RetrofitInstance
import com.example.netowrk_training.repository.NewsRepository
import com.example.netowrk_training.ui.MainActivity
import com.example.netowrk_training.ui.viewmodel.NewsProviderViewModel
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.example.netowrk_training.utils.AppStates
import com.example.netowrk_training.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.getValue
import kotlin.math.ceil


class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var binding : FragmentHomeBinding
    private val newsViewModel : NewsViewModel by activityViewModels {
        NewsProviderViewModel(requireActivity().application,NewsRepository(ArticleDatabase.invoke(requireContext())))
    }
    lateinit var newsAdapter: NewsListAdapter


    var isLoading = false
    var isError = false
    var isScroll = false
    var isLastPage = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setupNewsList()




            if (newsViewModel.headLines.value?.data?.articles.isNullOrEmpty()) {
                newsViewModel.getHeadLines("us")
            }






        newsAdapter.setOnItemClick {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id. action_homeFragment_to_articleFragment,bundle)
        }







        newsViewModel.headLines.observe(viewLifecycleOwner, Observer{
            response ->
            when(response){
                is AppStates.Error<*> -> {
                    hideProgressBar()
                    response.message?.let {
                        message->
                        Snackbar.make(view,"Error : $message", Snackbar.LENGTH_SHORT).show()
                        if(!isLastPage){
                            showErrorMessage(message)
                        }
                    }
                    if(!isLastPage){
                        showErrorMessage(response.message.toString())
                    }


                }
                is AppStates.Loading<*> -> {
                    hideErrorMessage()
                    showProgressBar()
                }
                is AppStates.Success<*> -> {
                    hideErrorMessage()
                    hideProgressBar()
                    response.data?.let {
                        newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.orEmpty())
                        val totalPage: Double = newsResponse.totalResults.toDouble() / Constants.QUERY_PAGE_SIZE
                        isLastPage = newsViewModel.headlinesPage - 1 == ceil(totalPage).toInt()
                        Log.d("totalPage", "totalPage : ${ceil(totalPage).toInt()}")
                        Log.d("totalPage", "totalRes : ${newsResponse.totalResults}")
                        Log.d("isLastPage", "headPage : ${newsViewModel.headlinesPage}")
                        if(isLastPage){
                            binding.headlinesList.setPadding(0,0,0,0)
                        }



                    }
                }
            }
        })

        binding.errorMessage.retryButton.setOnClickListener {
                newsViewModel.getHeadLines("us")
        }
    }


    private fun hideProgressBar(){
        binding.paginationProgressIndicator.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressIndicator.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage(){
//        binding.errorMessage.root.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String){
//        binding.errorMessage.root.visibility =  View.VISIBLE
//        binding.errorMessage.errorText.text = message
        isError = true
    }




    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            val isAtEnd = (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
            // val isListLongEnough = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val paginate: Boolean = !isError && !isLoading  && !isLastPage && isAtEnd && isScroll

            Log.d("totalItemCount", "totalItemCount : $totalItemCount")
            Log.d("isError","isError : $isError") // false
            Log.d("isLoading","isLoading : $isLoading") // false
            Log.d("isLastPage","isLastPage : $isLastPage") // false
            Log.d("isAtEnd","isAtEnd : $isAtEnd") // false
         //   Log.d("isListLongEnough","isListLongEnough : $isListLongEnough") // false
            Log.d("firstVisibleItemPosition","firstVisibleItemPosition : $firstVisibleItemPosition")

            if(paginate){
                newsViewModel.getHeadLines("us")
                isScroll = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            isScroll = true
        }
    }

    private fun setupNewsList(){
        newsAdapter = NewsListAdapter()

        binding.headlinesList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeFragment.scrollListener)
        }

    }

}