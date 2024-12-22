package com.example.dailyplanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_lists")
data class TaskListEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val tasksJson: String
)
