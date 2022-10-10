package com.example.android.hackernews.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.hackernews.data.daos.NewsItemDao
import com.example.android.hackernews.data.daos.TopStoryDao
import com.example.android.hackernews.data.entities.BestStories
import com.example.android.hackernews.data.entities.NewStories
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.data.entities.TopStory

/**
 * The Room database for this app
 * */
@Database(
    entities = [NewsItem::class, TopStory::class, NewStories::class, BestStories::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsItemDao(): NewsItemDao
    abstract fun topStoriesDao(): TopStoryDao

    companion object {
        // for singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            Log.d("AppDatabase", "Room database initiated")
            return Room.databaseBuilder(context, AppDatabase::class.java, "hacker-news-db")
                .fallbackToDestructiveMigration() // TODO: remove before release
                .build()
        }
    }
}