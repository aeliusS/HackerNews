package com.example.android.hackernews.utils

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.*
import com.example.android.hackernews.workers.RefreshDataWorker
import java.util.concurrent.TimeUnit

fun setupRecurringWork(context: Context, replace: Boolean = false) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    var updateInterval = sharedPreferences.getString("update_interval", "1440")?.toLongOrNull()
    if (updateInterval == null) updateInterval = 1440

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
        updateInterval,
        TimeUnit.MINUTES
    )
        .setConstraints(constraints)
        .setInitialDelay(updateInterval, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        RefreshDataWorker.TAG,
        if (replace) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP,
        repeatingRequest
    )
}