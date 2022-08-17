package com.example.timemanagement.homeScreen

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagement.loginScreen.LoginUiState
import com.example.timemanagement.models.Task
import com.example.timemanagement.repository.Resources
import com.example.timemanagement.repository.StorageRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
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

    fun loadTasks(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserTasks(userId)
            }
        }else{
            homeUiState = homeUiState.copy(tasks = Resources.Error(
                throwable = Throwable(message = "User is not Login")
            ))
        }
    }
    private fun getUserTasks(userId:String) = viewModelScope.launch {
        repository.getUserTasks(userId).collect {
            homeUiState = homeUiState.copy(tasks = it)
        }
    }


    fun getTask(taskId:String){
        repository.getTask(
            taskId = taskId,
            onError = {},
        ){
            homeUiState = homeUiState.copy(selectedTask = it!!)
        }
    }

    fun resetState(){
        homeUiState = HomeUIState()
    }


    fun signOut() = repository.signOut()


    fun OnChangeTitle(title: String) {
        homeUiState = homeUiState.copy(title = title)
    }

    fun OnChangeDesc(desc: String) {
        homeUiState = homeUiState.copy(desc = desc)
    }


    fun OnRemoveTask(taskId: String) = repository.deleteTask(taskId){
        loadTasks()
    }

    fun OnAddTask() {
        if (hasUser){
            repository.addTask(
                userId = user!!.uid,
                title = homeUiState.title,
                description = homeUiState.desc,
                timestamp = Timestamp.now()
            ){
                loadTasks()
            }
        }    }

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
            taskId = taskId,
        ){
            loadTasks()
        }


    }
    fun clearTask() {
        homeUiState = homeUiState.copy(selectedTask = Task(userId = "", title = "", description = ""))




    }


}