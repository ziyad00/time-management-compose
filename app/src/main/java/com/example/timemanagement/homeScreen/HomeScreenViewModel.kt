package com.example.timemanagement.homeScreen

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagement.loginScreen.LoginUiState
import com.example.timemanagement.models.Count
import com.example.timemanagement.models.Task
import com.example.timemanagement.repository.Resources
import com.example.timemanagement.repository.StorageRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.log

class HomeScreenViewModel(
    private val repository: StorageRepository = StorageRepository(),

    ) : ViewModel() {


    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    var homeUiState by mutableStateOf(HomeUIState())
        private set

    fun loadTasks() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserTasks(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                tasks = Resources.Error(
                    throwable = Throwable(message = "User is not Login")
                )
            )
        }
    }

    private fun getUserTasks(userId: String) = viewModelScope.launch {
        repository.getUserTasks(userId).collect {
            homeUiState = homeUiState.copy(tasks = it)
        }
    }


    fun getTask(taskId: String) {
        repository.getTask(
            taskId = taskId,
            onError = {},
        ) {
            homeUiState = homeUiState.copy(selectedTask = it!!)
        }
    }

    fun resetState() {
        homeUiState = HomeUIState()
    }


    fun signOut() = repository.signOut()


    fun OnChangeTitle(title: String) {
        homeUiState = homeUiState.copy(title = title)
    }

    fun OnChangeDesc(desc: String) {
        homeUiState = homeUiState.copy(desc = desc)
    }

    fun OnChangeTag(tag: String) {
        homeUiState = homeUiState.copy(tag = tag)
    }


    fun OnRemoveTask(taskId: String) = repository.deleteTask(taskId) {
        loadTasks()
    }

    fun OnAddTask() {
        if (hasUser) {
            repository.addTask(
                userId = user!!.uid,
                title = homeUiState.title,
                description = homeUiState.desc,
                status = false,
                tags = seperateTags(),
                count = Count(),
                timestamp = Timestamp.now()
            ) {
                loadTasks()
            }
        }
    }

    fun seperateTags(): List<String> {
        return homeUiState.tag.split(" ")
    }

    fun OnChangeSelectedTask(task: Task) {


        if (homeUiState.selectedTask.userId != "") {
            clearTask()
        }

        homeUiState = homeUiState.copy(selectedTask = task.copy())


    }

    fun OnUpdateTask(taskId: String) {


        repository.updateTask(
            title = homeUiState.title,
            desc = homeUiState.desc,
            tags = seperateTags(),
            taskId = taskId,
        ) {
            loadTasks()
        }


    }

    fun clearTask() {
        homeUiState =
            homeUiState.copy(selectedTask = Task(userId = "", title = "", description = ""))


    }

    fun onChangeIsUpdated(flag: Boolean) {
        homeUiState = homeUiState.copy(isUpdated = flag)

    }


    fun getDateHourly(seconds: Long): String {
//        val current = Timestamp.now().seconds - seconds
        val current =  seconds
        val hours = current / 3600
        val minutes = (current % 3600) / 60
        val seconds = current % 60


        return "${hours}:${minutes}:${seconds}"
    }

    fun onChangeStatus(task: Task) {
        if (task.status) {
            task.count.pauseTime = Timestamp.now()
            task.count.countTimeInSeconds +=

                 task.count.pauseTime.seconds - task.count.resumeTime.seconds

            Log.i("TAG",task.count.countTimeInSeconds.toString() )
            task.count.resumeTime = Timestamp.now()
            task.count.pauseTime = Timestamp.now()


        } else {
            task.count.resumeTime = Timestamp.now()
        }
        repository.updateTaskStatus(
            status = !task.status,
            count = task.count,
            taskId = task.documentId!!,
        ) {
            loadTasks()
        }
    }


    fun representTags(tags: List<String>): String {
        var repr = ""
        for (tag in tags) {
            repr += tag + " "
        }
        return repr
    }
}