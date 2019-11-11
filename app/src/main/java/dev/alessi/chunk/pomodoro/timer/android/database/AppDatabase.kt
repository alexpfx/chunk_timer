package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class, WorkUnit::class], version = 7)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    abstract fun workUnitDao(): WorkUnitDao

}