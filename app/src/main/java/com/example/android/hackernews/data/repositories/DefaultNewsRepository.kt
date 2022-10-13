package com.example.android.hackernews.data.repositories

import android.util.Log
import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.di.IoDispatcher
import com.example.android.hackernews.utils.toTopStories
import com.example.android.hackernews.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNewsRepository @Inject constructor(
    private val newsLocalDataSource: NewsLocalDataSource,
    private val service: HackerNewsService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    init {
        Log.d(TAG, "DefaultNewsRepository initiated")
    }

    suspend fun getTopStoryUpdateDate() = withContext(ioDispatcher) {
        newsLocalDataSource.getTopStoryUpdateDate()
    }

    suspend fun updateTopStoryIdsFromRemoteService() = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            val topStoryIds = service.getTopStoryIds().toTopStories()
            newsLocalDataSource.insertTopStoryIds(topStoryIds)
            newsLocalDataSource.refreshTopStoryIds(Calendar.getInstance())
        }
    }

    suspend fun updateTopStories() = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            val topStoryIds = newsLocalDataSource.getTopStoryIds()
            for (topStoryId in topStoryIds) {
                val newsItem = service.getNewsItem(topStoryId.itemId)
                newsLocalDataSource.upsertNewsItem(newsItem)
            }
        }
    }

    suspend fun updateTopStoriesWithComments() = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            getTopStories().collect { newsItems ->
                for (news in newsItems) {
                    getChildren(news)
                }
            }
        }
    }

    suspend fun getChildren(newsItem: NewsItem) {
        if (newsItem.kids.isNullOrEmpty()) return
        for (child in newsItem.kids) {
            val newsComment = service.getNewsItem(child)
            newsLocalDataSource.upsertNewsItem(newsComment)
            getChildren(newsComment)
        }
    }

    fun getTopStories() = newsLocalDataSource.getTopStories()
        .flowOn(ioDispatcher)

    // TODO: update bookmarked stories

    companion object {
        private const val TAG = "DefaultNewsRepository"
    }
}