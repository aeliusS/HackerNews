package com.example.android.hackernews.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_items")
data class NewsItem(
    @PrimaryKey val id: Long,
    @ColumnInfo val deleted: Boolean = false,
    @ColumnInfo val type: String?,
    @ColumnInfo(name = "by") val author: String?,
    @ColumnInfo val time: Long?,
    @ColumnInfo val text: String?,
    @ColumnInfo val dead: Boolean = false,
    @ColumnInfo val parent: Long?,
    @ColumnInfo val poll: Long?,
    @ColumnInfo val kids: List<Long>?,
    @ColumnInfo val url: String?,
    @ColumnInfo val score: Int?,
    @ColumnInfo val title: String?,
    @ColumnInfo val parts: List<Long>?,
    @ColumnInfo val descendants: Int?, // number of comments

    // additional custom field
    @ColumnInfo val bookmarked: Boolean = false
)