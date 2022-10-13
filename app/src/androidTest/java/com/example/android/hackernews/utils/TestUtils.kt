package com.example.android.hackernews.utils

import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.entities.TopStory
import java.util.Calendar

/**
 * [NewsItem] objects used for tests
 * */
val testNewsItems = arrayListOf(
    NewsItem(
        id = 1,
        type = "story",
        author = "author1",
        time = 1665659297,
        text = null,
        parent = null,
        poll = null,
        kids = arrayListOf(3, 5),
        url = null,
        score = 74,
        title = "News Title",
        parts = null,
        descendants = 3
    ),
    NewsItem(
        id = 2,
        type = "story",
        author = "author2",
        time = 1665662932,
        text = "This is news text!",
        parent = null,
        poll = null,
        kids = arrayListOf(6),
        url = "https://www.example2.com",
        score = 105,
        title = "Ask HN: example ask",
        parts = null,
        descendants = 1
    ),
    NewsItem(
        id = 3,
        type = "comment",
        author = "author3",
        time = 1665664761,
        text = "Example comment from author3",
        parent = 1,
        kids = arrayListOf(4),
    ),
    NewsItem(
        id = 4,
        type = "comment",
        author = "author4",
        time = 1665666160,
        text = "Example comment from author4",
        parent = 3,
        kids = null
    ),
    NewsItem(
        id = 5,
        type = "comment",
        author = "author5",
        time = 1665666388,
        text = "Example comment from author5",
        parent = 1,
        kids = null
    ),
    NewsItem(
        id = 6,
        type = "comment",
        author = "author6",
        time = 1665664543,
        text = "Example comment from author6",
        parent = 2,
        kids = null
    )
)
val testNewsItem = testNewsItems[0]
val testTopStoriesAsNewsItems = testNewsItems.filter {
    it.parent == null
}

/**
 * [TopStory] object used for tests
 * */

fun getTestTopStories(): List<TopStory> {
    val topStories: MutableList<TopStory> = mutableListOf()
    val newsItems = testNewsItems.filter {
        it.parent == null
    }
    val calendar = Calendar.getInstance()
    newsItems.forEachIndexed { index, newsItem ->
        topStories.add(TopStory(itemId = newsItem.id, rank = index, itemDate = calendar))
    }
    return topStories
}