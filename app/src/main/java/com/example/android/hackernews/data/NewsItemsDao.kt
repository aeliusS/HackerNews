package com.example.android.hackernews.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [NewsItems] class.
 */
@Dao
interface NewsItemsDao {
    @Query("SELECT * FROM news_items WHERE parent is null")
    fun getAllItems(): Flow<List<NewsItems>>
}