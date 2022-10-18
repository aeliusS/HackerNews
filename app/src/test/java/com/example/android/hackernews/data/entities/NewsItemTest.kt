package com.example.android.hackernews.data.entities

import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class NewsItemTest {

    @Test
    fun test_default_values() {
        val defaultNews = NewsItem(
            id = 1,
            type = "story",
            author = "author",
            time = 0,
            text = "This is news text!",
            parent = null,
            poll = null,
            kids = null,
            url = null,
            score = null,
            title = "News Title",
            parts = null,
            descendants = 1
        )
        assertFalse(defaultNews.dead)
        assertFalse(defaultNews.bookmarked)
        assertFalse(defaultNews.deleted)
    }

    @Test
    fun test_equals() {
        val childNews = NewsItem(id = 3, type = "comment3", author = "author3")
        val childNewsTwo = NewsItem(id = 4, type = "comment4", author = "author4")
        val childNewsTwoCopyWithChildren =
            NewsItem(id = 4, type = "comment4", author = "author4").apply {
                childNewsItems = listOf(childNews)
            }

        val newsWithNoChildren = NewsItem(id = 1, type = "story", author = "author")
        val newsWithNoChildrenCopy = NewsItem(id = 1, type = "story", author = "author")

        val newsWithChildren = NewsItem(id = 1, type = "story", author = "author").apply {
            childNewsItems = listOf(childNews, childNewsTwo)
        }
        val newsWithChildrenCopy = NewsItem(id = 1, type = "story", author = "author").apply {
            childNewsItems = listOf(childNews, childNewsTwo)
        }
        val newsWithChildrenTwo = NewsItem(id = 1, type = "story", author = "author").apply {
            childNewsItems = listOf(childNews, childNewsTwoCopyWithChildren)
        }

        assertFalse(newsWithNoChildren == newsWithChildren)
        assertTrue(newsWithNoChildren == newsWithNoChildrenCopy)

        assertFalse(newsWithChildren == newsWithChildrenTwo)
        assertTrue(newsWithChildren == newsWithChildrenCopy)
    }
}