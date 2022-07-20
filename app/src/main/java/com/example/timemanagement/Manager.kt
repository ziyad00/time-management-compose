package com.example.timemanagement

import androidx.compose.runtime.mutableStateListOf

object Manager {
    var tasks = mutableStateListOf(Task(title = "task 1", description = "desc"), Task(title = "task 2", description = "desc2"))

}