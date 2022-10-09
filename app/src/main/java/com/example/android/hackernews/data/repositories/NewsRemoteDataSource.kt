package com.example.android.hackernews.data.repositories

import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.data.entities.TopStories
import com.example.android.hackernews.utils.toTopStories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRemoteDataSource @Inject constructor(private val service: HackerNewsService) {
    suspend fun getTopStories(): Flow<TopStories> {
        return service.getTopStories().toTopStories().asFlow()
    }
}