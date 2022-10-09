package com.example.android.hackernews.data.repositories

import com.example.android.hackernews.data.daos.NewsItemDao
import com.example.android.hackernews.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling data operations
 * */
@Singleton
class NewsLocalDataSource @Inject constructor(
    private val newsItemDao: NewsItemDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun getNewsItem(itemId: Long) = newsItemDao.getItem(itemId)

    companion object {
        // for singleton instantiation
        @Volatile
        private var instance: NewsLocalDataSource? = null

        fun getInstance(newsItemDao: NewsItemDao, ioDispatcher: CoroutineDispatcher) {
            instance ?: synchronized(this) {
                instance ?: NewsLocalDataSource(newsItemDao, ioDispatcher).also { instance = it }
            }
        }
    }
}