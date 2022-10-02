package com.example.android.hackernews.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [NewsItem] class.
 */
@Dao
interface NewsItemDao {
    @Query("SELECT * FROM news_items WHERE parent is null")
    fun getAllItems(): Flow<List<NewsItem>>
}