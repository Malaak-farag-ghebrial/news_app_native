package com.example.netowrk_training.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netowrk_training.R
import com.example.netowrk_training.adapters.NewsListAdapter
import com.example.netowrk_training.databinding.FragmentHomeBinding
import com.example.netowrk_training.ui.MainActivity
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.example.netowrk_training.utils.AppStates
import com.example.netowrk_training.utils.Constants
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.locks.Condition


class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var binding : FragmentHomeBinding
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)


        newsViewModel = (activity as MainActivity).newsViewModel
        setupNewsList()

        newsAdapter.onItemClicked {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id. action_homeFragment_to_articleFragment,bundle)
        }

        if (newsViewModel.headLines.value?.data?.articles.isNullOrEmpty()) {
            newsViewModel.getHeadLines("us")
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
                        val totalPage = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE
                        isLastPage = newsViewModel.headlinesPage == totalPage
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
        binding.errorMessage.root.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String){
        binding.errorMessage.root.visibility =  View.VISIBLE
        binding.errorMessage.errorText.text = message
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

            Log.i(this::class.java.simpleName,"isError : $isError")
//            Log.i("isLoading","isLoading : $isLoading")
//            Log.i("isLastPage","isLastPage : $isLastPage")
//            Log.i("isAtEnd","isAtEnd : $isAtEnd")
//            Log.i("isListLongEnough","isListLongEnough : $isListLongEnough")
//            Log.i("firstVisibleItemPosition","firstVisibleItemPosition : $firstVisibleItemPosition")

            if(paginate){
                newsViewModel.getHeadLines("us")
                isScroll = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

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