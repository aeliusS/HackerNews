package com.example.android.hackernews.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.android.hackernews.BuildConfig
import com.example.android.hackernews.R
import com.example.android.hackernews.MainActivity
import com.example.android.hackernews.data.NotificationData

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"
private const val NOTIFICATION_ID = 0
private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())

fun sendNotification(context: Context, data: NotificationData) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID
    // before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    //create a pending intent that opens MainActivity when the user clicks on the notification
    val intent = Intent(context.applicationContext, MainActivity::class.java)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else PendingIntent.FLAG_UPDATE_CURRENT
    val pendingIntent = PendingIntent.getActivity(
        context.applicationContext,
        NOTIFICATION_ID,
        intent,
        flags
    )

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(
            context.getString(
                R.string.notification_content,
                data.quantity,
                data.keyword
            )
        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(NOTIFICATION_ID, notification)
}