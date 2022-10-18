package com.example.android.hackernews.newslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.databinding.ListItemNewsBinding
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener

class NewsListAdapter(private val clickListener: NewsClickListener) :
    ListAdapter<NewsItem, NewsListAdapter.NewsListViewHolder>(DiffCallBack) {

    companion object DiffCallBack : DiffUtil.ItemCallback<NewsItem>() {
        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem == newItem
        }
    }

    class NewsListViewHolder(private var binding: ListItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: NewsClickListener, newsItem: NewsItem) {
            binding.newsItem = newsItem
            binding.clickListener = listener
            binding.executePendingBindings()
        }
        companion object {
            // to create new view holders
            fun from(parent: ViewGroup): NewsListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemNewsBinding.inflate(layoutInflater, parent, false)
                return NewsListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        return NewsListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

class NewsClickListener(val clickListener: (newsItem: NewsItem) -> Unit) {
    fun onClick(newsItem: NewsItem) = clickListener(newsItem)
}