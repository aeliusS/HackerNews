package com.example.android.hackernews.newslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject internal constructor(
    private val newsRepository: DefaultNewsRepository
) : ViewModel() {

    val topStories: LiveData<List<NewsItem>> = newsRepository.getTopStories().asLiveData()

    // TODO: use mutable state flow to set recycler view list

    init {
        Log.d(TAG, "NewsListViewModel initiated")
        updateTopStories()
    }

    // TODO: add updating notification

    private fun updateTopStories() {
        viewModelScope.launch {
            if (!shouldUpdateTopStories()) return@launch
            Log.d(TAG, "updating top stories")
            // TODO: get result and display error
            newsRepository.updateTopStoryIdsRemote()
            newsRepository.updateTopStories()
            // newsRepository.updateTopStoriesWithComments() // too expensive

            // TODO: do update top stories partial
        }
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

    companion object {
        private const val TAG = "NewsListViewModel"
    }
}