package com.example.android.hackernews.commentslist.adapter

import com.example.android.hackernews.R
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.databinding.ListItemCommentHeaderBinding
import com.xwray.groupie.databinding.BindableItem

class CommentHeader constructor(private val newsItem: NewsItem) :
    BindableItem<ListItemCommentHeaderBinding>() {
    override fun bind(viewBinding: ListItemCommentHeaderBinding, position: Int) {
        viewBinding.newsItem = newsItem
    }

    override fun getLayout(): Int = R.layout.list_item_comment_header

    override fun getId(): Long {
        return newsItem.id
    }
}