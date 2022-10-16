package com.example.android.hackernews.commentslist.adapter

import com.example.android.hackernews.data.entities.NewsItem
import com.xwray.groupie.ExpandableGroup

class ExpandableCommentGroup constructor(
    newsItem: NewsItem,
    depth: Int = 0
) : ExpandableGroup(ExpandableCommentItem(newsItem, depth)) {
    init {
        if (newsItem.childNewsItem != null) {
            for (child in newsItem.childNewsItem!!) {
                child?.let { add(ExpandableCommentGroup(child, depth.plus(1))) }
            }
        }
    }
}