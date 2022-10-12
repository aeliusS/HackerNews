package com.example.android.hackernews.utils

import com.example.android.hackernews.data.entities.*

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

fun NewsItem.toUpdate(): NewsItemUpdate {
    return NewsItemUpdate(
        id = id,
        deleted= deleted,
        type=type,
        by = author,
        time=time,
        text=text,
        dead=dead,
        parent=parent,
        poll=poll,
        kids=kids,
        url=url,
        score=score,
        title=title,
        parts=parts,
        descendants=descendants,
    )
}