package com.example.timemanagement

import androidx.compose.runtime.mutableStateListOf
import com.example.timemanagement.models.Task

object Manager {
    var tasks = mutableStateListOf(Task(title = "task 1", desc = "desc"), Task(title = "task 2", desc = "desc2"))

}