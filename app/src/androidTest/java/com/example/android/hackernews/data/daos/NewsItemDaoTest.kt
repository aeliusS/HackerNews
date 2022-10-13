package com.example.android.hackernews.data.daos

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.hackernews.data.AppDatabase
import com.example.android.hackernews.utils.getTestTopStories
import com.example.android.hackernews.utils.testNewsItems
import com.example.android.hackernews.utils.testTopStoriesAsNewsItems
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsItemDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var newsItemDao: NewsItemDao

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun createDb() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        newsItemDao = database.newsItemDao()

        newsItemDao.upsertItems(testNewsItems)
        database.topStoriesDao().insertAll(getTestTopStories())
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetTopStories() = runBlocking {
        val topStories = newsItemDao.getTopStories().first()
        assertThat(topStories.size, equalTo(testTopStoriesAsNewsItems.size))
    }

    // TODO: test that bookmarked items don't get deleted
}