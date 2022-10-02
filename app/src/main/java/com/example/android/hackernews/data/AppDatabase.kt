package com.example.android.hackernews.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The Room database for this app
 * */
@Database(entities = [NewsItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsItemDao(): NewsItemDao

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
            return Room.databaseBuilder(context, AppDatabase::class.java, "hacker-news-db")
                .fallbackToDestructiveMigration() // TODO: remove before release
                .build()
        }
    }
}