package com.example.android.hackernews.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.hackernews.R
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.newslist.NewsListAdapter
import java.net.MalformedURLException
import java.net.URI

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

@BindingAdapter("displayURL")
fun displayURL(textView: TextView, url: String?) {
    // remove the url view if the url is empty
    if (url == null) {
        val visibilityTag = textView.getTag(R.id.visibility_tag)
        if (visibilityTag == true || visibilityTag == null) {
            textView.visibility = View.GONE
            textView.setTag(R.id.visibility_tag, false)
        }
        textView.text = ""
    } else {
        textView.visibility = View.VISIBLE
        textView.setTag(R.id.visibility_tag, true)
        textView.text = getURLString(url)
    }
}

fun getURLString(input: String): String {
    val baseURL: String = try {
        val url = URI.create(input)
        url.host
    } catch (ex: MalformedURLException) {
        Log.e("BindingAdapters", "Malformed URL found")
        "~MALFORMED URL~"
    }
    return baseURL
}