package com.example.android.hackernews.data.entities

import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class NewsItemTest {

    @Test
    fun test_default_values() {
        val defaultNews = NewsItem(
            id = 1,
            type="story",
            author = "author",
            time=0,
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
        val defaultNews = NewsItem(id = 1, type = "story", author = "author")
        val newsWithNoChildren = NewsItem(id = 1, type = "story", author = "author")
        val newsWithChildren = NewsItem(id = 1, type = "story", author = "author").apply {
            childNewsItems = listOf(defaultNews)
        }
        assertFalse(newsWithNoChildren == newsWithChildren)
        assertTrue(defaultNews == newsWithNoChildren)
    }
}