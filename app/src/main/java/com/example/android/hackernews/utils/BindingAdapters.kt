package com.example.android.hackernews.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.hackernews.R
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.newslist.NewsListAdapter

@BindingAdapter("linkIcon")
fun bindLinkIconImage(imageView: ImageView, url: String?) {
    if (url == null) {
        imageView.setImageResource(R.drawable.ic_baseline_comment_50)
    } else {
        imageView.setImageResource(R.drawable.ic_baseline_link_50)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<NewsItem>?) {
    val adapter = recyclerView.adapter as NewsListAdapter
    adapter.submitList(data)
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, data: List<NewsItem>?) {
    view.visibility = if (!data.isNullOrEmpty()) View.GONE else View.VISIBLE
}