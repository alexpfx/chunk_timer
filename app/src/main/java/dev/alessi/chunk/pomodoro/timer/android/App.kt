package dev.alessi.chunk.pomodoro.timer.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import dev.alessi.chunk.pomodoro.timer.android.database.AppDatabase
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepositoryImpl


class App : Application(), RepositoryProvider {


    lateinit var mTaskRepository: TaskRepository


    override fun getTaskRepository(): TaskRepository {
        return mTaskRepository
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ChunkServiceChannel"
        const val NOTIFICATION_ID = 10000

    }


    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(this, AppDatabase::class.java, "appdatabase").build()

        mTaskRepository = TaskRepositoryImpl(db.taskDao(), db.workUnitDao())

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