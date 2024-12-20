package com.example.dailyplanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateStart: String,
    val dateFinish: String,
    val name: String,
    val description: String
)
