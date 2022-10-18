package com.example.android.hackernews.api

import com.example.android.hackernews.data.entities.NewsItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsService {

    @GET("v0/topstories.json")
    suspend fun getTopStoryIds(): List<Long>

    @GET("v0/beststories.json")
    suspend fun getBestStories(): List<Long>

    @GET("v0/newstories.json")
    suspend fun getNewStories(): List<Long>

    @GET("v0/item/{item_id}.json")
    suspend fun getNewsItem(@Path(value = "item_id", encoded = true) itemId: Long): NewsItem?

    companion object {
        private const val BASE_URL = "https://hacker-news.firebaseio.com/"

        fun create(): HackerNewsService {
            val client = OkHttpClient.Builder().build()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(HackerNewsService::class.java)
        }
    }
}