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
import dev.alessi.chunk.pomodoro.timer.android.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class App : Application(), EstimateRepositoryProvider, TaskRepositoryProvider,
    SliceRepositoryProvider {

    private val db: AppDatabase by lazy {
        createDb()
    }

    private val mEstimationRepository: EstimationRepository by lazy {
        EstimationRepositoryImpl(db.workUnitDao())
    }

    private val mTaskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(db.taskDao())
    }

    private val mSliceRepository: SliceRepository by lazy {
        SliceRepositoryImpl(db.workUnitDao(), db.taskDao())
    }


    private val scope = CoroutineScope(Dispatchers.Main)


    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ChunkServiceChannel"
        const val NOTIFICATION_ID = 10000

    }


    override fun onCreate() {
        super.onCreate()

        createDb()

        testDb()

        createNotificationChannel()
    }

    private fun testDb() {
        scope.launch {

            val ss = withContext(Dispatchers.IO) {
                mSliceRepository.loadAllTaskSizes()
            }

            if (ss.isNullOrEmpty()) {
                insertSizes()
            }

        }
    }


    private val onCreateDbCallback = object : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {

            //TODO ver como fazer migracao.
            insertSizes()


        }

    }

    private fun insertSizes() {
        scope.launch {
            withContext(Dispatchers.Default) {

                mTaskRepository.storeTask(
                    Task(
                        uid = -1,
                        description = getString(R.string.message_hint_workunit_without_task),
                        dateCreated = Date()
                    )
                )

                mSliceRepository.storeTaskSize(
                    TaskSize(
                        0,
                        getString(R.string.label_sizes_pp)
                    )
                )
                mSliceRepository.storeTaskSize(
                    TaskSize(
                        1,
                        getString(R.string.label_sizes_p)
                    )
                )
                mSliceRepository.storeTaskSize(
                    TaskSize(
                        2,
                        getString(R.string.label_sizes_m)
                    )
                )
                mSliceRepository.storeTaskSize(
                    TaskSize(
                        3,
                        getString(R.string.label_sizes_g)
                    )
                )
                mSliceRepository.storeTaskSize(
                    TaskSize(
                        4,
                        getString(R.string.label_sizes_gg)
                    )
                )


            }
        }
    }

    private fun createDb(): AppDatabase {
        val db: AppDatabase =
            Room.databaseBuilder(this, AppDatabase::class.java, "nbd.db")
                .addCallback(onCreateDbCallback)
                .fallbackToDestructiveMigration().build()

        db.query("select 1", null) //

        return db


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Chunk Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(false)
            channel.enableLights(false)
            channel.setShowBadge(false)

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }


    override val estimationRepository: EstimationRepository
        get() = mEstimationRepository
    override val taskRepository: TaskRepository
        get() = mTaskRepository
    override val sliceRepository: SliceRepository
        get() = mSliceRepository


}