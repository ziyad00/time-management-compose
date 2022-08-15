package com.example.timemanagement.homeScreen

import com.example.timemanagement.models.Task

data class UIState(
    val title: String = "",
    val desc: String = "",
    val selectedTask: Task? = null,
    val tasks: Array<Task>? = null,
)
