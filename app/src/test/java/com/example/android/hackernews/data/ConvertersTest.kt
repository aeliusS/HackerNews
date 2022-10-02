package com.example.android.hackernews.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class ConvertersTest {
    private lateinit var list: List<Long>
    private lateinit var listString: String

    private var moshi: Moshi? = null
    private var jsonAdapter: JsonAdapter<List<Long>>? = null

    @Before
    fun setUp() {
        list = listOf(100, 12)
        moshi = Moshi.Builder().build()

        @OptIn(ExperimentalStdlibApi::class)
        jsonAdapter = moshi!!.adapter()

        listString = jsonAdapter!!.toJson(list)
    }

    @After
    fun tearDown() {
        moshi = null
        jsonAdapter = null
    }

    @Test
    fun longListToString() {
        assertEquals(jsonAdapter?.toJson(list), Converters().longListToString(list))
    }

    @Test
    fun stringToLongList() {
        assertEquals(list, Converters().stringToLongList(listString))
    }

    @Test
    fun nullLongListToString() {
        assertEquals(jsonAdapter?.toJson(null), Converters().longListToString(null))
    }

    @Test
    fun nullStringToLongList() {
        assertEquals(null, Converters().stringToLongList("null"))
    }
}