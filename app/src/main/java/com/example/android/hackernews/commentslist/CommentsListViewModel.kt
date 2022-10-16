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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CommentsListFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val headerItem = args.newsItem
    val comments: LiveData<List<NewsItem?>> =
        newsRepository.getAllChildrenFromLocal(headerItem.id).asLiveData()

    // TODO: make the fragment have control over when remote is called
    init {
        viewModelScope.launch {
            headerItem.let { newsRepository.getChildrenFromRemote(it) }
        }
    }
}