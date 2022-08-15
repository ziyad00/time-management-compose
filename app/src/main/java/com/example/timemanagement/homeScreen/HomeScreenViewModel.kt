package com.example.timemanagement.homeScreen

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.timemanagement.models.Task

class HomeScreenViewModel : ViewModel() {

    var title by mutableStateOf("")

    var desc by mutableStateOf("")
    var tasks = mutableStateListOf(
        Task(title = "task 1", desc = "desc"),
        Task(title = "task 2", desc = "desc2")
    )
    var selectedTask: Task by mutableStateOf(Task("",""))

    fun OnChangeTitle(it: String) {
        title = it
    }

    fun OnChangeDesc(it: String) {
        desc = it
    }

    fun OnRemoveTask(task: Task) {
        tasks.remove(task)
    }

    fun OnAddTask(title: String, desc: String) {
        tasks.add(Task(title, desc))
    }

    fun OnChangeSelectedTask(task: Task) {


        if (selectedTask.title != "") {
            clearTask()
        }

        selectedTask = task.copy()

    }
    fun OnUpdateTask(oldTask: Task, title: String, desc: String) {


        var index = tasks.indexOf(oldTask)

        tasks[index].title = title
        tasks[index].desc = desc


    }
    fun clearTask() {
        selectedTask.title = ""
        selectedTask.desc = ""


    }


}