package com.example.android.hackernews.data.repositories

import com.example.android.hackernews.data.daos.NewsItemDao
import com.example.android.hackernews.data.daos.TopStoryDao
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.entities.TopStory
import com.example.android.hackernews.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling data operations
 * */
@Singleton
class NewsLocalDataSource @Inject constructor(
    private val newsItemDao: NewsItemDao,
    private val topStoryDao: TopStoryDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun getNewsItem(itemId: Long) = newsItemDao.getItem(itemId)

    fun getTopStories() = newsItemDao.getTopStories()

    suspend fun addUpdateNewsItem(newsItem: NewsItem) = newsItemDao.upsertItem(newsItem)

    suspend fun insertTopStoryIds(topStories: List<TopStory>) = topStoryDao.insertAll(topStories)

    suspend fun refreshTopStoryIds(calendar: Calendar) = topStoryDao.refreshTopStories(calendar)

    suspend fun getTopStoryIds() = topStoryDao.getTopStoryIds()

    suspend fun getTopStoryUpdateDate() = topStoryDao.getUpdateDate()

    companion object {
        // for singleton instantiation
        @Volatile
        private var instance: NewsLocalDataSource? = null

        fun getInstance(
            newsItemDao: NewsItemDao,
            topStoryDao: TopStoryDao,
            ioDispatcher: CoroutineDispatcher
        ) {
            instance ?: synchronized(this) {
                instance ?: NewsLocalDataSource(
                    newsItemDao,
                    topStoryDao,
                    ioDispatcher
                ).also { instance = it }
            }
        }
    }
}