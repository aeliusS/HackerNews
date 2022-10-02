package com.example.android.hackernews.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkNewsItem(
    val id: Long,
    val deleted: Boolean = false,
    val type: String?,
    @Json(name = "by") val author: String?,
    val time: Long?,
    val text: String?,
    val dead: Boolean = false,
    val parent: Long?,
    val poll: Long?,
    val kids: List<Long>?,
    val url: String?,
    val score: Int?,
    val title: String?,
    val parts: List<Long>?,
    val descendants: Int?, // number of comments
)