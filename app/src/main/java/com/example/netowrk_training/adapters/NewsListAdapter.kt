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

class NewsListAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<NewsListAdapter.ArticleViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        val card = LayoutInflater.from(parent.context).inflate(R.layout.news_item_card,parent,false)
        return ArticleViewHolder(card)
     }

    private var onItemClickListener : ((Article)-> Unit)? = null
    override fun onBindViewHolder(
        holder: ArticleViewHolder,
        position: Int
    ) {
        val article = differ.currentList[position]

        image = holder.itemView.findViewById<ImageView>(R.id.article_image)
       title = holder.itemView.findViewById<TextView>(R.id.article_title)
       description = holder.itemView.findViewById<TextView>(R.id.description)
       source = holder.itemView.findViewById<TextView>(R.id.article_source)
       date = holder.itemView.findViewById<TextView>(R.id.article_date)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(image)
            title.text = article.title
            description.text = article.description
            source.text = article.source.name
            date.text = article.publishedAt

            setOnClickListener {
                onItemClickListener?.let{
                    it(article)
                }
            }
        }
        fun setOnItemClick(listener: (Article)-> Unit){
            onItemClickListener = listener
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


     class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view)
        lateinit var image: ImageView
        lateinit var title: TextView
        lateinit var description : TextView
        lateinit var source : TextView
        lateinit var date: TextView


       private val differCallback = object : DiffUtil.ItemCallback<Article>(){
           override fun areItemsTheSame(
               oldItem: Article,
               newItem: Article
           ): Boolean {
               return oldItem.url == newItem.url
           }

           override fun areContentsTheSame(
               oldItem: Article,
               newItem: Article
           ): Boolean {
              return oldItem == newItem
           }

       }


        val differ = AsyncListDiffer(this,differCallback)

}