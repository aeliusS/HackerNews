package com.example.android.hackernews.commentslist

import androidx.lifecycle.*
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsListViewModel @Inject internal constructor(
    private val newsRepository: DefaultNewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CommentsListFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val newsItemId = args.newsItemId
    val comments: LiveData<List<NewsItem?>> = newsRepository.getAllChildrenFromLocal(newsItemId).asLiveData()

    init {
        viewModelScope.launch {
            val newsItem = newsRepository.getNewsItem(newsItemId).first()
            newsRepository.getChildrenFromRemote(newsItem)
        }
    }
}