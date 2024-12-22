package com.example.dailyplanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskListDao {
    @Insert
    suspend fun insert(taskList: TaskListEntity)

    @Query("SELECT * FROM task_lists")
    suspend fun getAllTaskLists(): List<TaskListEntity>

    @Query("SELECT * FROM task_lists WHERE id = 0")
    suspend fun getTaskListById(): TaskListEntity?

    @Update
    suspend fun update(taskList: TaskListEntity)

    suspend fun addTaskToList(newTask: Task) {
        val taskListEntity = getTaskListById()
        if (taskListEntity != null) {
            val currentTasks = TaskListTypeConverter().toTaskList(taskListEntity.tasksJson)
            currentTasks.add(newTask)
            val updatedTasksJson = TaskListTypeConverter().fromTaskList(currentTasks)
            val updatedTaskListEntity = taskListEntity.copy(tasksJson = updatedTasksJson)
            update(updatedTaskListEntity)
        } else {
            val newTaskList = mutableListOf(newTask)
            val newTasksJson = TaskListTypeConverter().fromTaskList(newTaskList) // Преобразуем его в JSON

            val newTaskListEntity = TaskListEntity(
                tasksJson = newTasksJson
            )

            insert(newTaskListEntity)
        }
    }
}