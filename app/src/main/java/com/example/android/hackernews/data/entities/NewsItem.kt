package com.example.android.hackernews.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

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
    val bookmarked: Boolean = false,
    var isExpanded: Boolean = true
) : Parcelable {

    /**
     * couldn't include the ignored variable in the constructor
     * using @JvmOverloads is an option, but it introduces other compilation problems
     * b/c of default values
     * */
    @IgnoredOnParcel
    @Ignore
    var childNewsItems: List<NewsItem?>? = null

    // had to override equals b/c childNewsItem is not part of the default constructor
    override fun equals(other: Any?): Boolean {
        return (other is NewsItem) && (
                this.id == other.id &&
                        this.deleted == other.deleted &&
                        this.type == other.type &&
                        this.author == other.author &&
                        this.time == other.time &&
                        this.text == other.text &&
                        this.dead == other.dead &&
                        this.parent == other.parent &&
                        this.poll == other.poll &&
                        this.kids == other.kids &&
                        this.url == other.url &&
                        this.score == other.score &&
                        this.title == other.title &&
                        this.parts == other.parts &&
                        this.descendants == other.descendants &&
                        this.bookmarked == other.bookmarked &&
                        this.childNewsItems == other.childNewsItems &&
                        this.isExpanded == other.isExpanded
                )
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + deleted.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + dead.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + (poll?.hashCode() ?: 0)
        result = 31 * result + (kids?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (score ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (parts?.hashCode() ?: 0)
        result = 31 * result + (descendants ?: 0)
        result = 31 * result + bookmarked.hashCode()
        result = 31 * result + (childNewsItems?.hashCode() ?: 0)
        result = 31 * result + isExpanded.hashCode()
        return result
    }
}