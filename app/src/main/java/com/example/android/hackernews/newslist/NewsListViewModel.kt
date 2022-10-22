package com.example.android.hackernews.newslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.android.hackernews.data.ApiStatus
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import com.example.android.hackernews.utils.wrapApiStatusError
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject internal constructor(
    private val newsRepository: DefaultNewsRepository
) : ViewModel() {

    val topStories: LiveData<List<NewsItem>> = newsRepository.getTopStories().asLiveData()

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    suspend fun refreshTopStories(force: Boolean = false) = wrapApiStatusError(_apiStatus) {
        if (!shouldUpdateTopStories() && !force) {
            newsRepository.updateTopStoriesFromRemote(true)
            return@wrapApiStatusError
        }
        Log.d(TAG, "updating top stories")
        newsRepository.updateTopStoryIdsRemote()
        newsRepository.updateTopStoriesFromRemote()
    }

    /**
     * Decide whether to update top stories
     * */
    private suspend fun shouldUpdateTopStories(): Boolean {
        val dataCalendar = newsRepository.getTopStoryUpdateDate() ?: return true

        val actualCalendar = Calendar.getInstance()
        val diff = actualCalendar.timeInMillis - dataCalendar.timeInMillis
        val seconds = diff / 1000
        val minutes = seconds / 60

        // update if it's been longer than 10 minutes
        Log.d(TAG, "Minutes since last update: $minutes")
        if (minutes >= 10) return true
        return false
    }

    fun finishedDisplayingApiErrorMessage() {
        _apiStatus.value = ApiStatus.DONE
    }

    companion object {
        private const val TAG = "NewsListViewModel"
    }
}