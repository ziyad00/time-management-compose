package com.example.timemanagement

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import com.example.timemanagement.homeScreen.HomeUIState
import com.example.timemanagement.models.Task
import com.example.timemanagement.repository.Resources
import com.example.timemanagement.theme.TimeManagementTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel) {
    val homeUiState = viewModel?.homeUiState

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )

    val scaffoldStateBottomSheet = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
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

                if (homeUiState.selectedTask.userId == "") {
                    viewModel.onChangeIsUpdated(false)
                } else {
                    viewModel.onChangeIsUpdated(true)


                }


                val scope = rememberCoroutineScope()
                BottomSheetScaffold(
                    scaffoldState = scaffoldStateBottomSheet,
                    modifier = Modifier.fillMaxSize(),
                    sheetContent = {
                        SheetContent(
                            sheetState,
                            homeUiState = homeUiState,
                            viewModel = viewModel
                        )
                    },
                    sheetPeekHeight = 0.dp

                ) {
                    GreetingSection("Android")
                    TaskSection(
                        homeUiState,
                        sheetState,

                        viewModel
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
    homeUiState: HomeUIState,
    sheetState: BottomSheetState,
    viewModel: HomeScreenViewModel


) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)) {
        Box(modifier = Modifier.padding(bottom = 10.dp)) {
            Text(text = "Tasks", style = MaterialTheme.typography.h4)

        }
        LazyColumn {
            items(items = homeUiState.tasks.data.orEmpty(),
                { task: Task -> task.documentId!! }) { task ->
                val dismissState = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                        viewModel.OnRemoveTask(task.documentId!!)
                    }
                    true
                })
                SwipeToDismiss(
                    state = dismissState,
                    background = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> Color.White
                                else -> Color.Red
                            }
                        )
                        val alignment = Alignment.CenterEnd
                        val icon = Icons.Default.Delete

                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = Dp(20f)),
                            contentAlignment = alignment
                        ) {

                        }
                    },

                    dismissThresholds = { direction ->
                        FractionalThreshold(0.5f)
                    },
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .background(Color.Gray)
                            .padding(10.dp)
                            .clickable {
                                scope.launch {
                                    viewModel.OnChangeSelectedTask(task)
                                    viewModel.OnChangeTitle(task.title!!)
                                    viewModel.OnChangeDesc(task.description!!)

                                    sheetState.collapse()
                                    sheetState.expand()
                                }
                            }
                    ) {
                        Column() {
                            Text(
                                text = task.title!!,
                                style = MaterialTheme.typography.body1,
                                color = Color.White
                            )
                            Text(
                                text = task.description!!,
                                style = MaterialTheme.typography.body1,
                                color = Color.White
                            )
                            Text(
                                text = viewModel.representTags(task.tags),
                                style = MaterialTheme.typography.body1,
                                color = Color.White
                            )
                        }


                        Column() {
                            Text(
                                text = viewModel.getDateHourly(task.count.countTimeInSeconds),
                                style = MaterialTheme.typography.body1,
                                color = Color.White
                            )

                            Button(onClick = { viewModel.onChangeStatus(task) }) {
                                if(!task.status)
                                Icon(Icons.Filled.PlayArrow, "Play")
                                else
                                    Icon(Icons.Filled.Pause, "Pause")
                            }
                        }


                    }
                }



            }
        }
    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetContent(
    sheetState: BottomSheetState,
    homeUiState: HomeUIState,
    viewModel: HomeScreenViewModel
) {
    val scope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),

        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier) {


            TextField(
                value = homeUiState.title,
                onValueChange = viewModel::OnChangeTitle,
                label = { Text(text = "Title") },
            )
            TextField(
                value = homeUiState.desc,
                onValueChange = viewModel::OnChangeDesc,
                label = { Text(text = "Description") },
            )
            TextField(
                value = homeUiState.tag,
                onValueChange = viewModel::OnChangeTag,
                label = { Text(text = "Add a tag") },

                )


         //   Text(text = viewModel.representTags(viewModel.seperateTags()))
            Button(onClick = {
                if (!homeUiState.isUpdated) {

                    viewModel.OnAddTask()
                } else {
                    viewModel.OnChangeSelectedTask(Task("", homeUiState.title, homeUiState.desc))
                    viewModel.OnUpdateTask(homeUiState.selectedTask.documentId!!)
                }


                scope.launch {
                    viewModel.OnChangeTitle("")
                    viewModel.OnChangeDesc("")
                    viewModel.OnChangeTag("")
                    sheetState.collapse()
                }
            }) {
                Text(text = if (!homeUiState.isUpdated) "Add Task" else "Update Task")
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview2() {

    HomeScreen(viewModel(modelClass = HomeScreenViewModel::class.java))

}

