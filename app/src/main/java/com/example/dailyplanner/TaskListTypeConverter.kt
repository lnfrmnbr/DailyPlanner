package com.example.dailyplanner

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskListTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromTaskList(taskList: MutableList<Task>): String {
        return gson.toJson(taskList)
    }

    @TypeConverter
    fun toTaskList(taskListJson: String): MutableList<Task> {
        val type = object : TypeToken<MutableList<Task>>() {}.type
        return gson.fromJson(taskListJson, type)
    }
}
