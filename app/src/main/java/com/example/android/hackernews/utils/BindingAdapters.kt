package com.example.android.hackernews.utils

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.hackernews.R
import com.example.android.hackernews.commentslist.adapter.CommentHeader
import com.example.android.hackernews.commentslist.adapter.ExpandableCommentGroup
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.newslist.NewsListAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import java.net.MalformedURLException
import java.net.URI

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<NewsItem>?) {
    val adapter = recyclerView.adapter as NewsListAdapter
    adapter.submitList(data)
}

@BindingAdapter("commentsListHeader", "commentsList")
fun bindCommentsRecyclerView(
    recyclerView: RecyclerView,
    commentsListHeader: NewsItem?,
    commentsList: List<NewsItem?>?
) {
    val adapter = recyclerView.adapter as GroupieAdapter
    commentsListHeader?.let {
        adapter.updateAsync(listOf(
            Section(CommentHeader(it)).apply {
                commentsList?.let { comments ->
                    for (comment in comments) {
                        comment?.let { add(ExpandableCommentGroup(comment)) }
                    }
                }
            }
        ))
    }
}

@BindingAdapter("displayIfNotNull")
fun displayIfNotNull(view: View, data: String?) {
    view.visibility = if (data.isNullOrBlank()) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, data: List<NewsItem>?) {
    view.visibility = if (!data.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfListNotNull")
fun goneIfListNotNull(view: View, data: List<NewsItem?>?) {
    view.visibility = if (data != null) View.GONE else View.VISIBLE
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