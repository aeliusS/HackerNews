package com.example.android.hackernews.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.android.hackernews.data.entities.NewsItem
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [NewsItem] class.
 */
@Dao
interface NewsItemDao {
    @Query("SELECT * FROM news_items WHERE id = :itemId")
    fun getItem(itemId: Long): Flow<NewsItem>

    @Transaction
    @Query(NEWS_ITEMS_TOP_STORIES)
    fun getTopStories(): Flow<List<NewsItem>>

    @Transaction
    @Query(NEWS_ITEMS_NEW_STORIES)
    fun getNewStories(): Flow<List<NewsItem>>

    @Transaction
    @Query(NEWS_ITEMS_BEST_STORIES)
    fun getBestStories(): Flow<List<NewsItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsItems: List<NewsItem>)

    companion object {
        private const val NEWS_ITEMS_TOP_STORIES = """
            SELECT nt.* FROM news_items as nt
            INNER JOIN top_stories AS top ON top.item_id = nt.id
            ORDER BY top.id ASC
        """

        private const val NEWS_ITEMS_NEW_STORIES = """
            SELECT nt.* FROM news_items as nt
            INNER JOIN new_stories AS new ON new.item_id = nt.id
            ORDER BY new.id ASC
        """

        private const val NEWS_ITEMS_BEST_STORIES = """
            SELECT nt.* FROM news_items as nt
            INNER JOIN best_stories AS best ON best.item_id = nt.id
            ORDER BY best.id ASC
        """
    }
}