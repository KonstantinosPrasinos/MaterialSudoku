package com.example.multiplayersudoku.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import com.example.multiplayersudoku.R

class MatchNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var matchStartTimeMillis: Long = 0L

    companion object {
        const val CHANNEL_ID = "multiplayer_live_match"
        const val CHANNEL_NAME = "Live Match Updates"
        const val NOTIFICATION_ID = 1001
        private const val MAX_PROGRESS = 100
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Live progress and timer for ongoing multiplayer matches"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun startNotification(
        opponentName: String,
        startTimeMillis: Long = System.currentTimeMillis()
    ) {
        matchStartTimeMillis = startTimeMillis
        updateNotification(
            title = "Match vs $opponentName",
            statusText = "Match started",
            progress = 0
        )
    }

    fun updateProgress(
        opponentName: String,
        progress: Int,
        statusText: String = "Opponent is making moves..."
    ) {
        updateNotification(
            title = "Match vs $opponentName",
            statusText = statusText,
            progress = progress.coerceIn(0, MAX_PROGRESS)
        )
    }

    private fun updateNotification(
        title: String,
        statusText: String,
        progress: Int
    ) {
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            val progressStyle = Notification.ProgressStyle()
                .setProgress(progress)
                .setProgressTrackerIcon(
                    Icon.createWithResource(context, R.drawable.ic_chess_queen)
                )
            Notification.Builder(context, CHANNEL_ID)
                .setStyle(progressStyle)
        } else {
            Notification.Builder(context, CHANNEL_ID)
                .setProgress(MAX_PROGRESS, progress, false)
        }

        val notification = notificationBuilder
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(statusText)
            .setOngoing(true)
            .setUsesChronometer(true)
            .setChronometerCountDown(false)
            .setWhen(matchStartTimeMillis)
            .setShowWhen(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            notification.setShortCriticalText("Opp ${progress}%")
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    fun dismiss() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}