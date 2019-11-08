package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class, WorkUnit::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    abstract fun workUnitDao(): WorkUnitDao

}