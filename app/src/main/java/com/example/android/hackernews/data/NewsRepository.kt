package com.example.android.hackernews.data

import com.example.android.hackernews.data.daos.NewsItemDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling data operations
 * */
@Singleton
class NewsRepository @Inject constructor(private val newsItemDao: NewsItemDao) {

    fun getNewsItem(itemId: Long) = newsItemDao.getItem(itemId)

    companion object {
        // for singleton instantiation
        @Volatile private var instance: NewsRepository? = null

        fun getInstance(newsItemDao: NewsItemDao) {
            instance ?: synchronized(this) {
                instance ?: NewsRepository(newsItemDao).also { instance = it }
            }
        }
    }
}