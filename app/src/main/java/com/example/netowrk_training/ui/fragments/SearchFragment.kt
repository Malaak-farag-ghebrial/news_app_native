package com.example.netowrk_training.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netowrk_training.R
import com.example.netowrk_training.adapters.NewsListAdapter
import com.example.netowrk_training.databinding.FragmentHomeBinding
import com.example.netowrk_training.databinding.FragmentSearchBinding
import com.example.netowrk_training.ui.MainActivity
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.example.netowrk_training.utils.AppStates
import com.example.netowrk_training.utils.Constants
import com.example.netowrk_training.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var binding : FragmentSearchBinding
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsListAdapter

    var isLoading = false
    var isError = false
    var isScroll = false
    var isLastPage = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        newsViewModel = (activity as MainActivity).newsViewModel
        setupNewsList()

        newsAdapter.onItemClicked {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articleFragment,bundle)
        }

        var job: Job? = null
        binding.searchInput.addTextChangedListener {
           text ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                text?.let {
                    if(text.toString().isNotEmpty()){
                        newsViewModel.searchNews(text.toString())
                    }
                }

            }

        }


        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer{
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
                        val totalPage = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE
                        isLastPage = newsViewModel.headlinesPage == totalPage
                        if(isLastPage){
                            binding.searchList.setPadding(0,0,0,0)
                        }



                    }
                }
            }
        })

        binding.errorMessageSearch.retryButton.setOnClickListener {
            if(binding.searchInput.text.isNotEmpty()){
                newsViewModel.searchNews(binding.searchInput.text.toString())
            }else{
                hideErrorMessage()
            }
        }

    }


    private fun hideProgressBar(){
        binding.paginationProgressIndicatorSearch.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressIndicatorSearch.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage(){
        binding.errorMessageSearch.root.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String){
        binding.errorMessageSearch.root.visibility =  View.VISIBLE
        binding.errorMessageSearch.errorText.text = message
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
            val isListLongEnough = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val paginate: Boolean = !isError && !isLoading  && !isLastPage && isAtEnd && isListLongEnough && firstVisibleItemPosition >= 0

            if(paginate){
                newsViewModel.searchNews(binding.searchInput.text.toString())
                isScroll = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScroll = true
            }
        }
    }

    private fun setupNewsList(){
        newsAdapter = NewsListAdapter()

        binding.searchList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }

    }

}