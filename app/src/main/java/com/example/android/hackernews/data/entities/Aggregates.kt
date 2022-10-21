package com.example.android.hackernews.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

interface AggregateEntity {
    val id: Long
    val itemId: Long
    val itemDate: Calendar?
}

@Entity(tableName = "top_stories")
data class TopStory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long = 0L,
    @ColumnInfo(name = "item_id") override val itemId: Long,
    @ColumnInfo(name = "item_date") override var itemDate: Calendar? = null,
    val rank: Int
) : AggregateEntity

@Entity(tableName = "new_stories")
data class NewStories(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long = 0L,
    @ColumnInfo(name = "item_id") override val itemId: Long,
    @ColumnInfo(name = "item_date") override val itemDate: Calendar? = null
) : AggregateEntity

@Entity(tableName = "best_stories")
data class BestStories(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long = 0L,
    @ColumnInfo(name = "item_id") override val itemId: Long,
    @ColumnInfo(name = "item_date") override val itemDate: Calendar? = null
) : AggregateEntity