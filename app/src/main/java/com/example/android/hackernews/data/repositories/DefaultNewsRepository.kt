package com.example.android.hackernews.data.repositories

import android.util.Log
import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.di.IoDispatcher
import com.example.android.hackernews.data.Result
import com.example.android.hackernews.data.entities.TopStory
import com.example.android.hackernews.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNewsRepository @Inject constructor(
    private val newsLocalDataSource: NewsLocalDataSource,
    private val newsRemoteDataSource: NewsRemoteDataSource,
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

    suspend fun updateNewsItem(newsItem: NewsItem) = newsLocalDataSource.updateNewsItem(newsItem)

    @Suppress("UNCHECKED_CAST")
    suspend fun refreshNewsItem(newsItemId: Long) = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            when (val result = newsRemoteDataSource.getNewsItem(newsItemId)) {
                is Result.Success<*> -> {
                    val newsItem = result.data as NewsItem
                    newsLocalDataSource.upsertNewsItemPartial(newsItem)
                }
                is Result.Error -> Log.e(TAG, "Error getting news item: $newsItemId")
            }
        }
    }

    suspend fun getTopStoryUpdateDate() = withContext(ioDispatcher) {
        newsLocalDataSource.getTopStoryUpdateDate()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun updateTopStoryIdsRemote() = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            when (val result = newsRemoteDataSource.getTopStoryIds()) {
                is Result.Success<*> -> {
                    val topStoryIds = result.data as List<TopStory>
                    newsLocalDataSource.refreshTopStoryIds(topStoryIds)
                }
                is Result.Error -> Log.e(TAG, "Error getting top stories: ${result.message}")
            }
        }
    }

    suspend fun updateTopStoriesFromRemote(resume: Boolean = false) = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            val topStoryIds = when (resume) {
                true -> newsLocalDataSource.getTopStoryIdsUndated() ?: return@withContext
                false -> newsLocalDataSource.getTopStoryIds() ?: return@withContext
            }
            Log.d(TAG, "updateTopStoriesFromRemote called")
            val calendar = Calendar.getInstance()
            topStoryIds.forEach { topStoryId ->
                // break from loop if cancelled
                ensureActive()
                val newsItem = service.getNewsItem(topStoryId.itemId)
                newsItem?.let {
                    newsLocalDataSource.upsertNewsItemPartial(newsItem.apply {
                        this.rank = topStoryId.rank
                    })
                    newsLocalDataSource.updateTopStory(topStoryId.apply { itemDate = calendar })
                }
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
        newsItem.kids.forEachIndexed { index, child ->
            ioDispatcher.ensureActive()
            val newsComment = service.getNewsItem(child)
            newsComment?.let {
                newsLocalDataSource.upsertNewsItemPartial(newsComment.apply {
                    this.rank = index
                })
                getChildrenFromRemoteHelper(newsComment)
            }
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