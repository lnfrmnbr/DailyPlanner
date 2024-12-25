package com.example.dailyplanner

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskListDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskListDao: TaskListDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        taskListDao = database.taskListDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllTaskLists() = runBlocking {
        val taskListEntity = TaskListEntity(tasksJson = "[]")
        taskListDao.insert(taskListEntity)

        val taskLists = taskListDao.getAllTaskLists()
        assertEquals(1, taskLists.size)
        assertEquals(taskListEntity.tasksJson, taskLists[0].tasksJson)
    }

    @Test
    fun addTaskToList() = runBlocking {
        val initialTask = Task(dateStart = "2025-01-01", dateFinish = "2025-01-02", name = "Initial Task", description = "First Task")
        val taskListEntity = TaskListEntity(tasksJson = "[]")
        taskListDao.insert(taskListEntity)

        taskListDao.addTaskToList(initialTask)

        val taskLists = taskListDao.getAllTaskLists()
        assertEquals(1, taskLists.size)

        val updatedTasksJson = taskLists[0].tasksJson
        val currentTasks = TaskListTypeConverter().toTaskList(updatedTasksJson)
        assertEquals(1, currentTasks.size)
        assertEquals(initialTask.name, currentTasks[0].name)
    }
}
