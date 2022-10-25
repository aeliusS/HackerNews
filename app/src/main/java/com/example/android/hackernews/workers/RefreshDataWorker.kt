package com.example.android.hackernews.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import com.example.android.hackernews.utils.sendNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var newsRepository: DefaultNewsRepository

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "updating in the background")
            newsRepository.updateTopStoryIdsRemote()
            newsRepository.updateTopStoriesFromRemote()
            newsRepository.removeStaleStories()
            sendNotificationForKeyword()
            Log.d(TAG, "finished updating in the background")
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error refreshing database", ex)
            Result.failure()
        }
    }

    private fun sendNotificationForKeyword() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val notification = sharedPreferences.getBoolean("notifications", false)
        if (!notification) return

        val keywordSearch = sharedPreferences.getString("keyword_search", null)
        if (keywordSearch != null) {
            sendNotification(applicationContext, "Test")
        }
    }

    companion object {
        const val TAG = "RefreshDataWorker"
    }
}