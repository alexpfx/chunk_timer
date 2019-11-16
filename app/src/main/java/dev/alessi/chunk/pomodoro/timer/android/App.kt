package dev.alessi.chunk.pomodoro.timer.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.alessi.chunk.pomodoro.timer.android.database.AppDatabase
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.TaskSize
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepositoryImpl
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.Executors


class App : Application(), RepositoryProvider {

    lateinit var mTaskRepository: TaskRepository

    private val scope = CoroutineScope(Dispatchers.Main)


    override fun getTaskRepository(): TaskRepository {
        return mTaskRepository
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ChunkServiceChannel"
        const val NOTIFICATION_ID = 10000

    }


    override fun onCreate() {
        super.onCreate()

        createDb()

        createNotificationChannel()
    }


    private val onCreateDbCallback = object : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {

            super.onOpen(db)
        }


        override fun onCreate(db: SupportSQLiteDatabase) {

            //TODO ver como fazer migracao.
            scope.launch {
                withContext(Dispatchers.Default) {

                    mTaskRepository.storeTask(Task(uid = -1, description = getString(R.string.message_hint_workunit_without_task), dateCreated = Date()))

                    mTaskRepository.storeTaskSize(
                        TaskSize(
                            0,
                            getString(R.string.label_sizes_pp)
                        )
                    )
                    mTaskRepository.storeTaskSize(
                        TaskSize(
                            1,
                            getString(R.string.label_sizes_p)
                        )
                    )
                    mTaskRepository.storeTaskSize(
                        TaskSize(
                            2,
                            getString(R.string.label_sizes_m)
                        )
                    )
                    mTaskRepository.storeTaskSize(
                        TaskSize(
                            3,
                            getString(R.string.label_sizes_g)
                        )
                    )
                    mTaskRepository.storeTaskSize(
                        TaskSize(
                            4,
                            getString(R.string.label_sizes_gg)
                        )
                    )




                }
            }


        }

    }

    private fun createDb() {
        val db: AppDatabase =
            Room.databaseBuilder(this, AppDatabase::class.java, "nbd.db").addCallback(onCreateDbCallback)
                .fallbackToDestructiveMigration().build()

        db.query("select 1", null) //

        mTaskRepository = TaskRepositoryImpl(db.taskDao(), db.workUnitDao())


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