package com.example.android.hackernews.data

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import java.util.*

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

    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar?): Long? = calendar?.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long?): Calendar =
        Calendar.getInstance().apply {
            if (value != null) {
                timeInMillis = value
            }
        }

}