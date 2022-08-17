package com.example.timemanagement.homeScreen

import com.example.timemanagement.models.Task
import com.example.timemanagement.repository.Resources

data class HomeUIState(
    val title: String = "",
    val desc: String = "",
    val selectedTask: Task = Task(userId = "", title = "",description = ""),
    val tasks: Resources<List<Task>> = Resources.Loading(),

    )
