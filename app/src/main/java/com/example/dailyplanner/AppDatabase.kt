package com.example.dailyplanner

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters

@Database(entities = [TaskListEntity::class], version = 1)
@TypeConverters(TaskListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_lists"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
