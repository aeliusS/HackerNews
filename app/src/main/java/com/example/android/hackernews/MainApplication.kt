package com.example.android.hackernews

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.*
import com.example.android.hackernews.utils.setupRecurringWork
import com.example.android.hackernews.workers.RefreshDataWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(
                if (BuildConfig.DEBUG) Log.DEBUG
                else Log.ERROR
            )
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork(applicationContext)
    }

}