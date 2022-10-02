package com.example.android.hackernews.di

import android.content.Context
import com.example.android.hackernews.data.AppDatabase
import com.example.android.hackernews.data.NewsItemDao
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
    fun provideNewsItemDao(appDatabase: AppDatabase): NewsItemDao {
        return appDatabase.newsItemDao()
    }
}