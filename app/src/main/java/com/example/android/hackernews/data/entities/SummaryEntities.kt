package com.example.android.hackernews.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

interface NewsEntity {
    val id: Long
    val itemId: Long
}

@Entity(tableName = "top_stories")
data class TopStories(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long = 0L,
    @ColumnInfo(name = "item_id") override val itemId: Long
): NewsEntity

@Entity(tableName = "new_stories")
data class NewStories(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "item_id") override val itemId: Long
): NewsEntity

@Entity(tableName = "best_stories")
data class BestStories(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "item_id") override val itemId: Long
): NewsEntity
