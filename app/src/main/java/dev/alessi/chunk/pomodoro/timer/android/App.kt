package dev.alessi.chunk.pomodoro.timer.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jakewharton.threetenabp.AndroidThreeTen
import dev.alessi.chunk.pomodoro.timer.android.database.AppDatabase
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class App : Application(), EstimateRepositoryProvider, TaskRepositoryProvider,
    SliceRepositoryProvider, AppUtilsProvider {

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
        AndroidThreeTen.init(this)

        createDb()

        createNotificationChannel()
    }


    private val onCreateDbCallback = object : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            prePopulate()
        }

    }

    private fun prePopulate() {


        scope.launch {
            withContext(Dispatchers.Default) {

                mTaskRepository.storeTask(
                    Task(
                        uid = -1,
                        description = getString(R.string.message_hint_workunit_without_task),
                        dateCreated = Date()
                    )
                )

            }
        }
    }

    private fun createDb(): AppDatabase {
        val db: AppDatabase =
            Room.databaseBuilder(this, AppDatabase::class.java, "fafax.db")
                .addCallback(onCreateDbCallback)
                .fallbackToDestructiveMigration() //TODO remover e criar migrations
                .addMigrations()
                .build()

        db.query("select 1", null)


        return db
    }


    override fun getSizeName(index: Int): String {
        val sizesNames = listOf(
            R.string.label_sizes_pp,
            R.string.label_sizes_p,
            R.string.label_sizes_m,
            R.string.label_sizes_g,
            R.string.label_sizes_gg
        )

        if (index == -1) {
            return ""
        }

        return getString(sizesNames[index])

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