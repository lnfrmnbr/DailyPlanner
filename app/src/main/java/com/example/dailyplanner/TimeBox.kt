package com.example.dailyplanner

data class TimeBox (
        val time: Int,
        val taskList: MutableList<Task> = mutableListOf()
)