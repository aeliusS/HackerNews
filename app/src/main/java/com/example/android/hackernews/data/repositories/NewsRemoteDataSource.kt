package com.example.android.hackernews.data.repositories

import com.example.android.hackernews.api.HackerNewsService
import com.example.android.hackernews.data.entities.TopStory
import com.example.android.hackernews.utils.toTopStories
import com.example.android.hackernews.data.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRemoteDataSource @Inject constructor(private val service: HackerNewsService) {

    suspend fun getTopStoryIds(): Result<List<TopStory>> {
        return try {
            val topStoryIds = service.getTopStoryIds().toTopStories()
            Result.Success(topStoryIds)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

}