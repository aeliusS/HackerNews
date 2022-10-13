package com.example.android.hackernews.data.entities

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Calendar

@JsonClass(generateAdapter = true)
@Entity(tableName = "news_items")
data class NewsItem(
    @PrimaryKey val id: Long,
    @ColumnInfo val deleted: Boolean = false,
    @ColumnInfo val type: String? = null,
    @Json(name = "by")
    @ColumnInfo(name = "by")
    val author: String? = null,
    @ColumnInfo val time: Long? = null, // unix time
    @ColumnInfo val text: String? = null,
    @ColumnInfo val dead: Boolean = false,
    @ColumnInfo val parent: Long? = null,
    @ColumnInfo val poll: Long? = null,
    @ColumnInfo val kids: List<Long>? = null,
    @ColumnInfo val url: String? = null,
    @ColumnInfo val score: Int? = null,
    @ColumnInfo val title: String? = null,
    @ColumnInfo val parts: List<Long>? = null,
    @ColumnInfo val descendants: Int? = null, // number of comments

    // additional custom field
    @ColumnInfo val bookmarked: Boolean = false
) {
    fun titleSpanned(): Spanned {
        if (title == null) return "~NO TITLE~".toSpanned()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    fun timeString(): CharSequence {
        if (time == null) return "unknown time ago"
        return DateUtils.getRelativeTimeSpanString(
            time * 1000,
            Calendar.getInstance().timeInMillis,
            MINUTE_IN_MILLIS
        )
    }
}