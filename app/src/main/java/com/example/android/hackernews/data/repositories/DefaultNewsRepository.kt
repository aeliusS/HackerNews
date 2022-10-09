package com.example.android.hackernews.data.repositories

import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.di.IoDispatcher
import com.example.android.hackernews.utils.toTopStories
import com.example.android.hackernews.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNewsRepository @Inject constructor(
    private val newsLocalDataSource: NewsLocalDataSource,
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val service: HackerNewsService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private suspend fun updateTopStoriesFromRemoteService() = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            newsRemoteDataSource.getTopStories().collect {

            }

        }
    }
}