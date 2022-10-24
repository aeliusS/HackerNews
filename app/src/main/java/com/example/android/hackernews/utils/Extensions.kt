package com.example.android.hackernews.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.android.hackernews.data.ApiStatus
import com.example.android.hackernews.data.entities.*
import java.net.UnknownHostException

fun List<Long>.toTopStories(): List<TopStory> {
    return mapIndexed { index, element ->
        TopStory(itemId = element, rank = index)
    }
}

fun List<Long>.toBestStories(): List<BestStories> {
    return map {
        BestStories(itemId = it)
    }
}

fun List<Long>.toNewStories(): List<NewStories> {
    return map {
        NewStories(itemId = it)
    }
}

fun NewsItem.toPartial(): NewsItemPartial {
    return NewsItemPartial(
        id = id,
        deleted = deleted,
        type = type,
        by = author,
        time = time,
        text = text,
        dead = dead,
        parent = parent,
        poll = poll,
        kids = kids,
        url = url,
        score = score,
        title = title,
        parts = parts,
        descendants = descendants,
        rank = rank
    )
}

inline fun <T> wrapApiStatusError(status: MutableLiveData<ApiStatus>, function: () -> T) {
    if (status.value == ApiStatus.LOADING) {
        Log.w("wrapApiStatusError", "API in loading status")
        return
    }
    status.value = ApiStatus.LOADING
    try {
        function()
    } catch (e: UnknownHostException) {
        Log.w("wrapApiStatusError", "exception: ${e.localizedMessage}")
        status.value = ApiStatus.ERROR
    } finally {
        if (status.value != ApiStatus.ERROR) status.value = ApiStatus.DONE
    }
}