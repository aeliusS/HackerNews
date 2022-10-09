package com.example.android.hackernews.data

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

/**
 * Type converters to allow Room to reference complex data types
 * */
object Converters {
    private val moshi: Moshi = Moshi.Builder().build()
    @OptIn(ExperimentalStdlibApi::class)
    private val jsonAdapter: JsonAdapter<List<Long>> = moshi.adapter()

    @TypeConverter
    fun longListToString(value: List<Long>?): String {
        return jsonAdapter.toJson(value)
    }

    @TypeConverter
    fun stringToLongList(value: String): List<Long>? {
        return jsonAdapter.fromJson(value)
    }


}