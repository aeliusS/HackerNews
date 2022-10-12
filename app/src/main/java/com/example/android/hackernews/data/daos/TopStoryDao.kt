package com.example.android.hackernews.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.android.hackernews.data.entities.TopStory
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Dao
interface TopStoryDao {

    @Query("SELECT item_date FROM top_stories LIMIT 1")
    suspend fun getUpdateDate(): Calendar?

    @Query("SELECT * FROM top_stories WHERE item_date IS NOT NULL")
    suspend fun getTopStoryIds(): List<TopStory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopStory(topStory: TopStory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topStories: List<TopStory>)

    @Query("DELETE FROM top_stories WHERE item_date IS NOT NULL")
    suspend fun deleteAllDatedTopStories()

    @Query("UPDATE top_stories SET item_date = :calendar")
    suspend fun updateAllWithDate(calendar: Calendar)

    @Transaction
    suspend fun refreshTopStories(calendar: Calendar) {
        deleteAllDatedTopStories()
        updateAllWithDate(calendar)
    }
}