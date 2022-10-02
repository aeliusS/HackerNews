package com.example.android.hackernews.di

import com.example.android.hackernews.api.HackerNewsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHackerNewsService(): HackerNewsService {
        return HackerNewsService.create()
    }
}