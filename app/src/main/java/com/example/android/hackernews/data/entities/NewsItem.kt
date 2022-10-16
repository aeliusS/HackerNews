package com.example.android.hackernews.data.entities

import android.os.Build
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "news_items")
data class NewsItem(
    @PrimaryKey val id: Long,
    val deleted: Boolean = false,
    val type: String? = null,
    @Json(name = "by")
    @ColumnInfo(name = "by")
    val author: String? = null,
    val time: Long? = null, // unix time
    val text: String? = null,
    val dead: Boolean = false,
    val parent: Long? = null,
    val poll: Long? = null,
    val kids: List<Long>? = null,
    val url: String? = null,
    val score: Int? = null,
    val title: String? = null,
    val parts: List<Long>? = null,
    val descendants: Int? = null, // number of comments

    // additional custom field
    val bookmarked: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    @Ignore var childNewsItem: List<NewsItem?>? = null

    fun titleSpanned(): Spanned {
        if (title == null) return "~NO TITLE~".toSpanned()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    fun textSpanned(): Spanned {
        if (text == null) return "~NO COMMENT~".toSpanned()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
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