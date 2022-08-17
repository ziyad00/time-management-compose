package com.example.timemanagement

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagement.homeScreen.HomeScreenViewModel
import com.example.timemanagement.models.Task
import com.example.timemanagement.repository.Resources
import com.example.timemanagement.theme.TimeManagementTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {
    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldStateBottomSheet = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val homeUiState = viewModel?.homeUiState
    LaunchedEffect(key1 = Unit) {
        viewModel?.loadTasks()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("Taskly") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            viewModel.clearTask()
                            sheetState.expand()
                        } else {
                            sheetState.collapse()
                        }
                    }
                },
                content = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        },
    ) {
        TimeManagementTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    GreetingSection("Android")
                    TaskSection(
                        homeUiState.tasks,
                        sheetState,

                        viewModel::OnRemoveTask,
                        viewModel::OnChangeSelectedTask,
                        viewModel::OnChangeTitle,
                        viewModel::OnChangeDesc,
                    )
                    AddEditTaskBottomSheet(
                        sheetState,
                        scaffoldStateBottomSheet,
                        title = homeUiState.title,
                        desc = homeUiState.desc,
                        OnChangeTitle = viewModel::OnChangeTitle,
                        OnChangeDesc = viewModel::OnChangeDesc,
                        OnAddTask = viewModel::OnAddTask,
                        selectedTask = homeUiState.selectedTask,
                        OnUpdateTask = viewModel::OnUpdateTask,
                        OnChangeSelectedTask = viewModel::OnChangeSelectedTask,

                        )

                }
            }
        }
    }

}


@Composable
fun GreetingSection(name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "Hello $name!", style = MaterialTheme.typography.h6)
        Text(text = "Profile", style = MaterialTheme.typography.h6)


    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskSection(
    tasks: Resources<List<Task>> ,
    sheetState: BottomSheetState,
    OnRemoveTask: (String) -> Unit,
    OnChangeSelectedTask: (Task) -> Unit,
    OnChangeTitle: (String) -> Unit,
    OnChangeDesc: (String) -> Unit,


    ) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)) {
        Box(modifier = Modifier.padding(bottom = 10.dp)) {
            Text(text = "Tasks", style = MaterialTheme.typography.h4)

        }
        LazyColumn {
            itemsIndexed(tasks.data.orEmpty()) { index, task ->

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .background(Color.Gray)
                        .padding(10.dp)
                        .clickable {
                            scope.launch {
                                OnChangeSelectedTask(task)
                                OnChangeTitle(task.title!!)
                                OnChangeDesc(task.description!!)

                                sheetState.collapse()
                                sheetState.expand()
                            }
                        }
                ) {
                    Text(
                        text = task.title!!,
                        style = MaterialTheme.typography.body1,
                        color = Color.White
                    )
                    Column() {
                        Text(
                            text = task.description!!,
                            style = MaterialTheme.typography.body1,
                            color = Color.White
                        )
                        Button(onClick = { OnRemoveTask(task.documentId!!) }) {
                            Text(text = "Delete")

                        }
                    }

                }


            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditTaskBottomSheet(
    sheetState: BottomSheetState,
    scaffoldStateBottomSheet: BottomSheetScaffoldState,
    title: String,
    desc: String,
    OnChangeTitle: (String) -> Unit,
    OnChangeDesc: (String) -> Unit,
    OnAddTask: () -> Unit,
    OnUpdateTask: (String) -> Unit,
    OnChangeSelectedTask: (Task) -> Unit,
    selectedTask: Task,
) {
    var isUpdate = false
    var oldTask = Task("", "", "")
    if (selectedTask.userId == "") {

        isUpdate = false
    } else {
        isUpdate = true
        oldTask = selectedTask.copy()

    }


    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = scaffoldStateBottomSheet,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {

        },
        sheetPeekHeight = 0.dp

    ) {
        
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetContent(
    sheetState: BottomSheetState,
    title: String,
                 OnChangeTitle: (String) -> Unit,
                 desc: String,
                 OnChangeDesc: (String) -> Unit,
                 OnAddTask: () -> Unit,
                 OnUpdateTask: (String) -> Unit,
                 OnChangeSelectedTask: (Task) -> Unit,
                 selectedTask: Task,

                 ) {
    val scope = rememberCoroutineScope()

    var isUpdate = false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),

        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier) {


            TextField(
                value = title,
                onValueChange = OnChangeTitle,
                label = { Text(text = "Title") },
            )
            TextField(
                value = desc,
                onValueChange = OnChangeDesc,
                label = { Text(text = "Description") },
            )
            Button(onClick = {
                if (!isUpdate) {

                    OnAddTask()
                } else {
                    OnChangeSelectedTask(Task("", title, desc))
                    Log.i("A", selectedTask.toString())
                    OnUpdateTask(selectedTask.documentId!!)
                }


                scope.launch {
                    OnChangeTitle("")
                    OnChangeDesc("")
                    sheetState.collapse()
                }
            }) {
                Text(text = if (!isUpdate) "Add Task" else "Update Task")
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview2() {

    HomeScreen()

}

