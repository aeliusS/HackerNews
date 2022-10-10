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
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject internal constructor(
    private val newsRepository: DefaultNewsRepository
): ViewModel() {

    val topStories: LiveData<List<NewsItem>> = newsRepository.getTopStories().asLiveData()

    init {
        Log.d(TAG, "NewsListViewModel initiated")
        updateTopStories()
    }

    private fun updateTopStories() {
        Log.d(TAG, "updating top stories")
        viewModelScope.launch {
            newsRepository.updateTopStoryIdsFromRemoteService()
            newsRepository.updateTopStories()
        }
    }

    companion object {
        private const val TAG = "NewsListViewModel"
    }
}