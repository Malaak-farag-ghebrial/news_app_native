package com.example.netowrk_training.ui.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netowrk_training.R
import com.example.netowrk_training.adapters.NewsListAdapter
import com.example.netowrk_training.database.ArticleDatabase
import com.example.netowrk_training.databinding.FragmentFavoriteBinding
import com.example.netowrk_training.repository.NewsRepository
import com.example.netowrk_training.ui.MainActivity
import com.example.netowrk_training.ui.viewmodel.NewsProviderViewModel
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.example.netowrk_training.utils.AppStates
import com.example.netowrk_training.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue


class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    lateinit var binding: FragmentFavoriteBinding
    private val newsViewModel : NewsViewModel by activityViewModels {
        NewsProviderViewModel(requireActivity().application,NewsRepository(ArticleDatabase.invoke(requireContext())))
    }
    lateinit var newsAdapter: NewsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)

        setupFavoriteList()

        newsAdapter.setOnItemClick {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_articleFragment, bundle)
        }

//        newsViewModel.getHeadLines("us")
        binding.favTextHead.setOnClickListener {
            newsViewModel.getFavoriteArticle()
        }
        val itemOnTouch = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val index = viewHolder.bindingAdapterPosition
                val article = newsAdapter.differ.currentList[index]

                newsViewModel.deleteArticleFromFavorite(article)
                Snackbar.make(view, "Item Deleted", Snackbar.LENGTH_SHORT).setAction("Undo") {
                    newsViewModel.addToFavorite(article)
                }.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top

                // Get resources (ensure these are defined in your project)
                val context = recyclerView.context
                val deleteIcon = ContextCompat.getDrawable(context, R.drawable.trash) // Your delete icon
                val background = ColorDrawable()
                if(dX == 0f){
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }
                background.color = Color.RED
                if(dX > 0){
                    background.setBounds(itemView.left,itemView.top,itemView.right + dX.toInt(),itemView.bottom)
                }
                else{
                    background.setBounds(itemView.left + dX.toInt(),itemView.top,itemView.right,itemView.bottom)
                }
                background.draw(c)

                if (deleteIcon != null) {
                    val intrinsicWidth = deleteIcon.intrinsicWidth
                    val intrinsicHeight = deleteIcon.intrinsicHeight

                    // Calculate position of the icon
                    val iconMargin = (itemHeight - intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + intrinsicHeight

                    if (dX > 0) { // Swiping Right
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = itemView.left + iconMargin + intrinsicWidth
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    } else { // Swiping Left
                        val iconLeft = itemView.right - iconMargin - intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    }
                    deleteIcon.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

            }



        }

        ItemTouchHelper(itemOnTouch).attachToRecyclerView(binding.favoriteList)

        newsViewModel.getFavoriteArticle().observe(viewLifecycleOwner,Observer{
            articles -> newsAdapter.differ.submitList(articles)

        })

    }


    private fun setupFavoriteList() {
        newsAdapter = NewsListAdapter()

        binding.favoriteList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }


}