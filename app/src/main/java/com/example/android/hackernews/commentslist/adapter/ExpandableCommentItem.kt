package com.example.android.hackernews.commentslist.adapter

import com.example.android.hackernews.R
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.databinding.ListItemCommentsBinding
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.databinding.BindableItem

class ExpandableCommentItem constructor(
    private val newsItem: NewsItem,
    private val depth: Int
): BindableItem<ListItemCommentsBinding>(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    override fun getLayout(): Int = R.layout.list_item_comments

    override fun bind(viewBinding: ListItemCommentsBinding, position: Int) {
        viewBinding.newsItem = newsItem
        viewBinding.expandCommentArrow.setOnClickListener {
            expandableGroup.onToggleExpanded()
            // TODO: animate down arrow
        }
    }

    private fun addingDepthView(viewBinding: ListItemCommentsBinding) {
        //viewBinding.
    }
}