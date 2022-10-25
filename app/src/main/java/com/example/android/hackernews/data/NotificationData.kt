package com.example.android.hackernews.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(val keyword: String, val quantity: Int) : Parcelable