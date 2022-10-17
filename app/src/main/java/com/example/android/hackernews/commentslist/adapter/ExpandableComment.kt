package com.example.android.hackernews.commentslist.adapter

import android.util.Log
import com.example.android.hackernews.R
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.databinding.ListItemCommentsBinding
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class ExpandableComment constructor(
    private val newsItem: NewsItem,
    private val depth: Int
) : BindableItem<ListItemCommentsBinding>(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    override fun getLayout(): Int = R.layout.list_item_comments

    override fun bind(viewBinding: ListItemCommentsBinding, position: Int) {
        viewBinding.newsItem = newsItem
        viewBinding.expandCommentArrow.setOnClickListener {
            expandableGroup.onToggleExpanded()
            Log.d("ExpandableComment", "expanded for item: ${newsItem.id}")
        }
        viewBinding.executePendingBindings()
    }

    override fun getId(): Long {
        return newsItem.id
    }

    // TODO: bug with equality
    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is ExpandableComment && other.newsItem == newsItem
    }

    private fun addingDepthView(viewBinding: ListItemCommentsBinding) {
        //viewBinding.
    }
}