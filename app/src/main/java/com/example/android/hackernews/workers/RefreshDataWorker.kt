package com.example.android.hackernews.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.hackernews.R
import com.example.android.hackernews.data.NotificationData
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
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var newsRepository: DefaultNewsRepository

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "updating in the background")
            newsRepository.updateTopStoryIdsRemote()
            newsRepository.updateTopStoriesFromRemote()
            newsRepository.removeStaleStories()
            sendKeywordNotification()

            Log.d(TAG, "finished updating in the background")
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error refreshing database", ex)
            ex.localizedMessage?.let { sendErrorNotification(it) }
            Result.failure()
        }
    }

    private suspend fun sendKeywordNotification() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val notification = sharedPreferences.getBoolean("notifications", false)
        if (!notification) return

        val keywordSearch = sharedPreferences.getString("keyword_search", null)
        if (keywordSearch != null) {
            val newsItems = newsRepository.getItemsWithKeyword(keywordSearch)
            if (newsItems.isEmpty()) return

            val title = applicationContext.getString(R.string.notification_title)
            val content = applicationContext.getString(
                R.string.notification_content,
                newsItems.size,
                keywordSearch
            )
            sendNotification(applicationContext, NotificationData(title, content))
        }
    }

    private fun sendErrorNotification(errorString: String) {
        val errorTitle = applicationContext.getString(R.string.worker_error_title)
        val errorContent = applicationContext.getString(R.string.worker_error_content, errorString)
        sendNotification(applicationContext, NotificationData(errorTitle, errorContent))
    }

    companion object {
        const val TAG = "RefreshDataWorker"
    }
}