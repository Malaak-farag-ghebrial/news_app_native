package com.example.netowrk_training.adapters

import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.netowrk_training.R
import com.example.netowrk_training.models.Article

class NewsListAdapter() : RecyclerView.Adapter<NewsListAdapter.ArticleViewHolder>() {

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item_card, parent, false)
        return ArticleViewHolder(card)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        // bind data
        Glide.with(holder.itemView).load(article.urlToImage).into(holder.image)
        holder.title.text = article.title
        holder.description.text = article.description
        holder.source.text = article.source?.name?:""
        holder.date.text = article.publishedAt

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(article)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnItemClick(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.article_image)
        val title: TextView = view.findViewById(R.id.article_title)
        val description: TextView = view.findViewById(R.id.description)
        val source: TextView = view.findViewById(R.id.article_source)
        val date: TextView = view.findViewById(R.id.article_date)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)
}
