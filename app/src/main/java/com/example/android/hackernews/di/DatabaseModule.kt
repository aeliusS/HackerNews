package com.example.android.hackernews.di

import android.content.Context
import com.example.android.hackernews.data.AppDatabase
import com.example.android.hackernews.data.NewsItemsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideNewsItemsDao(appDatabase: AppDatabase): NewsItemsDao {
        return appDatabase.newsItemsDao()
    }
}