package com.example.android.hackernews.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(val title: String, val content: String, val error: Boolean = false) :
    Parcelable