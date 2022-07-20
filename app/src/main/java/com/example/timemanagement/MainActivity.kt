package com.example.timemanagement

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagement.ui.theme.TimeManagementTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val scaffoldState = rememberScaffoldState()
            Scaffold(
                modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState,
                topBar = {
                    TopAppBar(title = { Text("Title") })
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onClick,
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
                            TasksSection()
                            AddTaskBottomSheet()

                        }
                    }
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

@Composable
fun TasksSection() {

    Column(modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)) {
        Box(modifier = Modifier.padding(bottom = 10.dp)) {
            Text(text = "Tasks", style = MaterialTheme.typography.h4)

        }
        task(Manager.tasks)


    }
}

@Composable
fun task(tasks: List<Task>) {
    LazyColumn {
        items(tasks) { task ->

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = task.title!!, style = MaterialTheme.typography.body1)
                Column() {
                    Text(
                        text = task.description!!,
                        style = MaterialTheme.typography.body1
                    )
                    Button(onClick = { Manager.tasks.remove(task) }) {
                        Text(text = "Click me pls")

                    }
                }

            }


        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddTaskBottomSheet() {

    var title by rememberSaveable  { mutableStateOf("") }
    var description by rememberSaveable  { mutableStateOf("") }

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                .height(300.dp),

            contentAlignment = Alignment.Center
            ) {
                Column() {
                    Text(text = " ")

                    TextField(
                        value = title, onValueChange = {title =it},
                        label = { Text(text = "Time") },

                    )
                    TextField(value = description,
                        onValueChange = {description = it},
                        label = { Text(text = "Time") },
                    )
                    Button(onClick = {
                        Manager.tasks.add(Task(title = title, description = description))
                        scope.launch {
                            title = ""
                            description = ""
                            sheetState.collapse()
                        }
                    }) {
                        Text(text = "Add Task")
                    }
                }

            }
        },
        sheetPeekHeight = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                scope.launch {
                    if(sheetState.isCollapsed) {
                        sheetState.expand()
                    } else {
                        sheetState.collapse()
                    }
                }
            }) {
                Text(text = "Bottom sheet")
            }
        }
    }
}

val onClick = {
    Manager.tasks.add(Task(title = "task", description = "dd"))
    Log.i("Task", Manager.tasks.toString())
    Unit
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val scaffoldState = rememberScaffoldState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("Title") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClick,
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
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    GreetingSection("Android")
                    TasksSection()


                }
            }
        }

    }
}