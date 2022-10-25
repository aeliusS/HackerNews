package com.example.android.hackernews.commentslist

import androidx.lifecycle.*
import com.example.android.hackernews.data.ApiStatus
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import com.example.android.hackernews.utils.wrapApiStatusError
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

    val comments: LiveData<List<NewsItem>?> = headerItem.switchMap { newsItem ->
        newsRepository.getAllChildrenFromLocal(newsItem.id).asLiveData()
    }

    /*
    val headerAndComments: LiveData<List<NewsItem>> = headerItem.asFlow().combine(comments.asFlow()) { header, comments ->
        mutableListOf(header).apply {
            comments?.let { comments ->
                this.addAll(comments)
            }
        }
    }.asLiveData()
     */

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    suspend fun updateHeaderAndComments() = wrapApiStatusError(_apiStatus) {
        headerItem.value?.let {
            newsRepository.refreshNewsItem(it.id, it.rank)
            newsRepository.getChildrenFromRemote(it)
        }
    }

    fun toggleIsExpanded(newsItem: NewsItem) = viewModelScope.launch {
        newsRepository.updateNewsItem(newsItem.copy(isExpanded = !newsItem.isExpanded))
    }

    fun finishedDisplayingApiErrorMessage() {
        _apiStatus.value = ApiStatus.DONE
    }
}