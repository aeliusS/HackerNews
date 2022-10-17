package com.example.android.hackernews.commentslist.adapter

import com.example.android.hackernews.data.entities.NewsItem
import com.xwray.groupie.ExpandableGroup

class ExpandableCommentGroup constructor(
    newsItem: NewsItem,
    depth: Int = 0
) : ExpandableGroup(ExpandableComment(newsItem, depth)) {
    init {
        if (newsItem.childNewsItems != null) {
            for (child in newsItem.childNewsItems!!) {
                child?.let { add(ExpandableCommentGroup(child, depth.plus(1))) }
            }
        }
    }
}