package com.example.android.hackernews.utils

import com.example.android.hackernews.data.entities.BestStories
import com.example.android.hackernews.data.entities.NewStories
import com.example.android.hackernews.data.entities.TopStory

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