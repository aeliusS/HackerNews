package com.example.android.hackernews.data.repositories

import android.util.Log
import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.di.IoDispatcher
import com.example.android.hackernews.data.Result
import com.example.android.hackernews.data.entities.TopStory
import com.example.android.hackernews.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
            val topStories = getTopStories().first()
            topStories.forEach {
                ensureActive()
                getChildrenFromRemote(it)
            }
        }
    }

    suspend fun getChildrenFromRemote(newsItem: NewsItem) = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            getChildrenFromRemoteHelper(newsItem, newsItem.id)
        }
    }

    private suspend fun getChildrenFromRemoteHelper(newsItem: NewsItem, topLevelParent: Long) {
        if (newsItem.kids.isNullOrEmpty()) return

        newsItem.kids.forEachIndexed { index, child ->
            yield() // in case of cancel
            val newsComment = service.getNewsItem(child)
            newsComment?.let {
                newsLocalDataSource.upsertNewsItemPartial(newsComment.apply {
                    this.rank = index
                    this.topLevelParent = topLevelParent
                })
                getChildrenFromRemoteHelper(newsComment, topLevelParent)
            }
        }
    }

    fun getAllChildrenFromLocal(newsItemId: Long) =
        newsLocalDataSource.getChildItems(newsItemId)
            .onEach { topChildItems ->
                if (topChildItems == null) return@onEach
                for (child in topChildItems) {
                    child.childNewsItems = getChildrenFromLocal(child)
                }
            }
            .flowOn(ioDispatcher)

    private suspend fun getChildrenFromLocal(newsItem: NewsItem): List<NewsItem>? {
        // it queries the same table, so using first() here should be fine
        val childItems = newsLocalDataSource.getChildItems(newsItem.id).first() ?: return null
        for (child in childItems) {
            child.childNewsItems = getChildrenFromLocal(child)
        }
        return childItems
    }

    suspend fun removeStaleStories() = newsLocalDataSource.removeStaleStories()


    companion object {
        private const val TAG = "DefaultNewsRepository"
    }
}