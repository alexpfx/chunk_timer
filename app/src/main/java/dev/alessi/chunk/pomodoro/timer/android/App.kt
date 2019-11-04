package dev.alessi.chunk.pomodoro.timer.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build


class App : Application() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ChunkServiceChannel"
        const val NOTIFICATION_ID = 10000

    }


    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Chunk Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.enableVibration(false)
            channel.enableLights(false)
            channel.setShowBadge(false)

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
}