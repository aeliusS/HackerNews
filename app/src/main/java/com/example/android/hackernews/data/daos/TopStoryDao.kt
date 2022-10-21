package com.example.android.hackernews.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.android.hackernews.data.entities.TopStory
import java.util.Calendar

@Dao
interface TopStoryDao {

    @Query(
        "SELECT item_date FROM top_stories " +
                "WHERE item_date IS NOT NULL ORDER BY item_date DESC LIMIT 1"
    )
    suspend fun getUpdateDate(): Calendar?

    /** Query used to resume update **/
    @Query("SELECT * FROM top_stories WHERE item_date IS NULL ORDER BY rank ASC")
    suspend fun getTopStoryIdsUndated(): List<TopStory>?

    @Query("SELECT * FROM top_stories ORDER BY rank ASC")
    suspend fun getTopStoryIds(): List<TopStory>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topStories: List<TopStory>)

    @Query("DELETE FROM top_stories")
    suspend fun deleteAll()

    @Update
    suspend fun update(topStory: TopStory)

    @Transaction
    suspend fun refreshTopStories(topStories: List<TopStory>) {
        deleteAll()
        insertAll(topStories)
    }
}