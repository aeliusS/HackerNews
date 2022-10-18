package com.example.android.hackernews.data.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.entities.NewsItemUpdate
import com.example.android.hackernews.utils.toUpdate
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [NewsItem] class.
 */
@Dao
interface NewsItemDao {

    @Insert
    suspend fun insertItem(newsItem: NewsItem)

    @Update(entity = NewsItem::class)
    suspend fun updateItemPartial(newsItemUpdate: NewsItemUpdate)

    @Update
    suspend fun updateItem(newsItem: NewsItem)

    suspend fun upsertItem(newsItem: NewsItem) {
        try {
            insertItem(newsItem)
        } catch (e: SQLiteConstraintException) {
            updateItemPartial(newsItem.toUpdate())
        }
    }

    // only use to setup tests. This has performance hit for large lists
    suspend fun upsertItems(newsItems: List<NewsItem>) {
        newsItems.forEach {
            try {
                insertItem(it)
            } catch (e: SQLiteConstraintException) {
                updateItemPartial(it.toUpdate())
            }
        }
    }

    @Query(REMOVE_STALE_NEWS)
    suspend fun removeStaleStories()

    @Query("SELECT * FROM news_items WHERE id = :itemId")
    fun getItem(itemId: Long): Flow<NewsItem>

    @Query("SELECT * FROM news_items WHERE id IN (:itemIds)")
    fun getItems(itemIds: List<Long>): Flow<List<NewsItem>>

    @Query(GET_CHILD_COMMENTS)
    fun getChildItems(itemId: Long): Flow<List<NewsItem?>>

    @Transaction
    @Query(NEWS_ITEMS_TOP_STORIES)
    fun getTopStories(): Flow<List<NewsItem>>

    @Transaction
    @Query(NEWS_ITEMS_NEW_STORIES)
    fun getNewStories(): Flow<List<NewsItem>>

    @Transaction
    @Query(NEWS_ITEMS_BEST_STORIES)
    fun getBestStories(): Flow<List<NewsItem>>

    companion object {
        // TODO: switch to rank order
        private const val GET_CHILD_COMMENTS = """
            SELECT * FROM news_items 
            WHERE parent IN (:itemId) AND (dead IS NULL OR dead IS FALSE)
                AND (deleted IS NULL OR deleted IS FALSE)
            ORDER BY id ASC
        """

        private const val NEWS_ITEMS_TOP_STORIES = """
            SELECT nt.* FROM news_items as nt
            INNER JOIN top_stories AS top ON top.item_id = nt.id
            WHERE top.item_date IS NOT NULL 
                    AND (nt.dead IS null OR nt.dead IS FALSE)
            ORDER BY top.rank ASC
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

        private const val REMOVE_STALE_NEWS = """
            DELETE FROM news_items
            WHERE id IN (
                SELECT nt.id FROM news_items AS nt
                LEFT JOIN top_stories AS top ON top.item_id = nt.id
                LEFT JOIN new_stories AS new ON new.item_id = nt.id
                LEFT JOIN best_stories AS best ON best.item_id = nt.id
                WHERE top.item_id IS null AND new.item_id IS null 
                    AND best.item_id IS null AND nt.bookmarked IS FALSE
            )
        """
    }
}