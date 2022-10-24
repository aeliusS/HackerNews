package com.example.android.hackernews.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.hackernews.data.repositories.DefaultNewsRepository
import com.example.android.hackernews.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshDataWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val newsRepository: DefaultNewsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            newsRepository.updateTopStoryIdsRemote()
            newsRepository.updateTopStoriesFromRemote()
            newsRepository.removeStaleStories()
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error refreshing database", ex)
            Result.failure()
        }
    }

    private suspend fun sendNotificationForKeyword() {
        TODO()
    }

    companion object {
        private const val TAG = "RefreshDataWorker"
    }
}