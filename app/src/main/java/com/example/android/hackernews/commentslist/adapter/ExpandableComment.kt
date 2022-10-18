package com.example.android.hackernews.commentslist.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.android.hackernews.R
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.databinding.LayoutSeparatorViewBinding
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
        addingDepthView(viewBinding)

        viewBinding.newsItem = newsItem
        viewBinding.expandCommentArrow.setOnClickListener {
            // TODO: update newsItem.isExpanded in database
            expandableGroup.onToggleExpanded()
            Log.d("ExpandableComment", "expanded for item: ${newsItem.id}")
        }
        viewBinding.executePendingBindings()
    }

    override fun getId(): Long {
        return newsItem.id
    }

    // TODO: bug with equality in groupie
    // see https://github.com/lisawray/groupie/issues/379
//    override fun hasSameContentAs(other: Item<*>): Boolean {
//        return other is ExpandableComment && other.newsItem == newsItem && other.newsItem.childNewsItems == newsItem.childNewsItems
//    }

    private fun addingDepthView(viewBinding: ListItemCommentsBinding) {
        val separatorContainer = viewBinding.separatorContainer
        separatorContainer.removeAllViews()
        separatorContainer.visibility = if (depth > 0) View.VISIBLE else View.GONE
        for (i in 1..depth) {
            val view: View = LayoutInflater.from(viewBinding.root.context)
                .inflate(R.layout.layout_separator_view, separatorContainer, false)
            separatorContainer.addView(view)
        }
        viewBinding.commentText.requestLayout()
    }


}