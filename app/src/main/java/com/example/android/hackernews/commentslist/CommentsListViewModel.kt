package com.example.android.hackernews.commentslist

import androidx.lifecycle.*
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsListViewModel @Inject internal constructor(
    private val newsRepository: DefaultNewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CommentsListFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _headerItem: MutableLiveData<NewsItem> = MutableLiveData(args.newsItem)
    val headerItem: LiveData<NewsItem>
        get() = _headerItem

    val comments: LiveData<List<NewsItem?>> = headerItem.switchMap { newsItem ->
        newsRepository.getAllChildrenFromLocal(newsItem.id).asLiveData()
    }

    suspend fun updateHeader() {
        headerItem.value?.let {
            newsRepository.refreshNewsItem(it.id)
        }
    }

    suspend fun getComments() {
        headerItem.value?.let {
            newsRepository.getChildrenFromRemote(it)
        }
    }

    fun toggleIsExpanded(newsItem: NewsItem) = viewModelScope.launch {
        newsRepository.updateNewsItem(newsItem.copy(isExpanded = !newsItem.isExpanded))
    }
}