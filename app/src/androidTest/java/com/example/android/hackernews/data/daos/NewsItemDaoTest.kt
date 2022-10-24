package com.example.android.hackernews.data.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.filters.MediumTest
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

@MediumTest
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

    @Test
    fun testGetChildItems() = runBlocking {
        val topStory = newsItemDao.getTopStories().first()[0]
        val childNewsItems = newsItemDao.getChildItems(topStory.id).first()

        assertThat(topStory.kids!!.size, equalTo(childNewsItems!!.size))
        for (child in childNewsItems) {
            assertThat(topStory.kids!!.contains(child.id), equalTo(true))
        }
    }

    @Test
    fun testGetChildItems_emptyData() = runBlocking {
        val childItems = newsItemDao.getChildItems(-1).first()
        assertThat(childItems?.size ?: 0, equalTo(0))
    }

    // TODO: test stale stories

    // TODO: test that bookmarked items don't get deleted
}