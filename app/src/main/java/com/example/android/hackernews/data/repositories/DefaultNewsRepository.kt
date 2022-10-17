package com.example.android.hackernews.data.repositories

import android.util.Log
import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.di.IoDispatcher
import com.example.android.hackernews.utils.toTopStories
import com.example.android.hackernews.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    fun getNewsItem(newsItemId: Long) = newsLocalDataSource.getNewsItem(newsItemId)
        .flowOn(ioDispatcher)


    fun getTopStories() = newsLocalDataSource.getTopStories()
        .flowOn(ioDispatcher)

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

    suspend fun updateTopStoriesWithCommentsFromRemote() = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            getTopStories().collect { newsItems ->
                for (news in newsItems) {
                    getChildrenFromRemote(news)
                }
            }
        }
    }

    suspend fun getChildrenFromRemote(newsItem: NewsItem) = withContext(ioDispatcher) {
        getChildrenFromRemoteHelper(newsItem)
    }

    private suspend fun getChildrenFromRemoteHelper(newsItem: NewsItem) {
        if (newsItem.kids.isNullOrEmpty()) return
        // var newsComments: MutableList<NewsItem> = mutableListOf()
        for (child in newsItem.kids) {
            val newsComment = service.getNewsItem(child)
            newsLocalDataSource.upsertNewsItem(newsComment)
            getChildrenFromRemoteHelper(newsComment)
        }
    }

    fun getAllChildrenFromLocal(newsItemId: Long) =
        newsLocalDataSource.getChildItems(newsItemId)
            .onEach { topChildItems ->
                for (child in topChildItems) {
                    child?.childNewsItems = child?.let { getChildrenFromLocal(it) }
                }
            }
            .flowOn(ioDispatcher)

    private suspend fun getChildrenFromLocal(newsItem: NewsItem): List<NewsItem?> {
        // it queries the same table, so using first() here should be fine
        val childItems = newsLocalDataSource.getChildItems(newsItem.id).first()
        for (child in childItems) {
            child?.childNewsItems = child?.let { getChildrenFromLocal(it) }
        }
        return childItems
    }

    // TODO: update bookmarked stories

    companion object {
        private const val TAG = "DefaultNewsRepository"
    }
}