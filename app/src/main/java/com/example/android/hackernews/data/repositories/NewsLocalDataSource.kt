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

    fun getChildItems(itemId: Long) = newsItemDao.getChildItems(itemId)

    fun getTopStories() = newsItemDao.getTopStories()

    suspend fun updateTopStory(topStory: TopStory) = topStoryDao.update(topStory)

    suspend fun upsertNewsItemPartial(newsItem: NewsItem) = newsItemDao.upsertItem(newsItem)

    suspend fun updateNewsItem(newsItem: NewsItem) = newsItemDao.updateItem(newsItem)

    suspend fun refreshTopStoryIds(topStories: List<TopStory>) =
        topStoryDao.refreshTopStories(topStories)

    suspend fun getTopStoryIds() = topStoryDao.getTopStoryIds()

    suspend fun getTopStoryUpdateDate() = topStoryDao.getUpdateDate()

    suspend fun getTopStoryIdsUndated() = topStoryDao.getTopStoryIdsUndated()

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