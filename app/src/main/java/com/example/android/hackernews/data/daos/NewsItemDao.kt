package com.example.android.hackernews.data.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.entities.NewsItemPartial
import com.example.android.hackernews.utils.toPartial
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [NewsItem] class.
 */
@Dao
interface NewsItemDao {

    @Insert
    suspend fun insertItem(newsItem: NewsItem)

    @Update
    suspend fun updateItem(newsItem: NewsItem)

    @Update(entity = NewsItem::class)
    suspend fun updateItemPartial(newsItemPartial: NewsItemPartial)

    suspend fun upsertItem(newsItem: NewsItem) {
        try {
            insertItem(newsItem)
        } catch (e: SQLiteConstraintException) {
            updateItemPartial(newsItem.toPartial())
        }
    }

    @VisibleForTesting
    suspend fun upsertItems(newsItems: List<NewsItem>) {
        newsItems.forEach {
            try {
                insertItem(it)
            } catch (e: SQLiteConstraintException) {
                updateItemPartial(it.toPartial())
            }
        }
    }

    @Query("SELECT id FROM news_items WHERE title LIKE '%' || :keyword || '%'")
    suspend fun getItemIdsWithKeyword(keyword: String): List<Long>

    @Query(GET_CHILD_COMMENTS)
    fun getChildItems(itemId: Long): Flow<List<NewsItem>?>

    @Transaction
    @Query(NEWS_ITEMS_TOP_STORIES)
    fun getTopStories(): Flow<List<NewsItem>>

    @Transaction
    @Query(NEWS_ITEMS_NEW_STORIES)
    fun getNewStories(): Flow<List<NewsItem>>

    @Transaction
    @Query(NEWS_ITEMS_BEST_STORIES)
    fun getBestStories(): Flow<List<NewsItem>>

    @Query(REMOVE_STALE_NEWS)
    suspend fun removeStaleStories()

    companion object {
        private const val GET_CHILD_COMMENTS = """
            SELECT * FROM news_items 
            WHERE parent IN (:itemId) AND (dead IS NULL OR dead IS FALSE)
                AND (deleted IS NULL OR deleted IS FALSE)
            ORDER BY rank ASC
        """

        private const val NEWS_ITEMS_TOP_STORIES = """
            SELECT nt.* FROM news_items as nt
            INNER JOIN top_stories AS top ON top.item_id = nt.id
            WHERE nt.dead IS null OR nt.dead IS FALSE
            ORDER BY nt.rank ASC
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
                LEFT JOIN top_stories AS top ON top.item_id = nt.id OR top.item_id = nt.top_level_parent
                LEFT JOIN new_stories AS new ON new.item_id = nt.id
                LEFT JOIN best_stories AS best ON best.item_id = nt.id
                WHERE top.item_id IS null AND new.item_id IS null 
                    AND best.item_id IS null AND nt.bookmarked IS FALSE
            )
        """
    }
}