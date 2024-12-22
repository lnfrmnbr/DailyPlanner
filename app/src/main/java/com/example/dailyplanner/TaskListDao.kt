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

    @Query("SELECT * FROM task_lists LIMIT 1")
    suspend fun getFirstTaskList(): TaskListEntity?

    @Update
    suspend fun update(taskList: TaskListEntity)

    @Query("DELETE FROM task_lists")
    suspend fun deleteAllTaskLists()

    suspend fun addTaskToList(newTask: Task) {
        val taskListEntity = getFirstTaskList()
        if (taskListEntity != null) {
            val currentTasks = TaskListTypeConverter().toTaskList(taskListEntity.tasksJson)
            currentTasks.add(newTask)
            val updatedTasksJson = TaskListTypeConverter().fromTaskList(currentTasks)
            val updatedTaskListEntity = taskListEntity.copy(id = taskListEntity.id, tasksJson = updatedTasksJson)
            update(updatedTaskListEntity)
        } else {
            val newTaskList = mutableListOf(newTask)
            val newTasksJson = TaskListTypeConverter().fromTaskList(newTaskList)
            val newTaskListEntity = TaskListEntity(tasksJson = newTasksJson)
            insert(newTaskListEntity)
        }
    }
}
