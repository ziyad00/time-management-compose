package com.example.timemanagement

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.timemanagement.homeScreen.HomeViewModel
import com.example.timemanagement.homeScreen.HomeUIState
import com.example.timemanagement.models.Task
import com.example.timemanagement.theme.TimeManagementTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel? = null,
               navController: NavHostController = rememberNavController(),

               ) {
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
//        topBar = {
//            TopAppBar(title = { Text("Taskly") })
//        },
        bottomBar = {
            BottomAppBar {

                BottomAppBar(
                    modifier = Modifier
                        .height(65.dp)
                        .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                    cutoutShape = CircleShape,
                    //backgroundColor = Color.White,
                    elevation = 22.dp
                ) {
                    BottomNav(navController = navController)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,

        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                modifier = Modifier,
                onClick = {
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            viewModel?.clearTask()
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

                if (homeUiState?.selectedTask?.userId == "") {
                    viewModel.onChangeIsUpdated(false)
                } else {
                    viewModel?.onChangeIsUpdated(true)


                }


                val scope = rememberCoroutineScope()
                BottomSheetScaffold(
                    scaffoldState = scaffoldStateBottomSheet,
                    modifier = Modifier.fillMaxSize(),
                    sheetContent = {
                        SheetContent(
                            sheetState,
                            homeUiState = homeUiState,
                            viewModel = viewModel,
                        )
                    },
                    sheetPeekHeight = 0.dp

                ) {
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
    homeUiState: HomeUIState?,
    sheetState: BottomSheetState,
    viewModel: HomeViewModel?


) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)) {
//        Box(modifier = Modifier.padding(bottom = 10.dp)) {
//            Text(text = "Tasks", style = MaterialTheme.typography.h4)
//
//        }
        LazyColumn {
            items(items = homeUiState?.tasks?.data.orEmpty(),
                { task: Task -> task.documentId!! }) { task ->
                val dismissState = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                        viewModel?.OnRemoveTask(task.documentId!!)
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
                                    viewModel?.OnChangeSelectedTask(task)
                                    viewModel?.OnChangeTitle(task.title!!)
                                    viewModel?.OnChangeDesc(task.description!!)

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
                                text = viewModel?.representTags(task.tags)  ?: "",
                                style = MaterialTheme.typography.body1,
                                color = Color.White
                            )
                        }


                        Column() {
                            Text(
                                text = viewModel?.getDateHourly(task.count.countTimeInSeconds) ?: "",
                                style = MaterialTheme.typography.body1,
                                color = Color.White
                            )

                            Button(onClick = { viewModel?.onChangeStatus(task) }) {
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
    homeUiState: HomeUIState?,
    viewModel: HomeViewModel?
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
                value = homeUiState?.title  ?: "",
                onValueChange = {viewModel?.OnChangeTitle(it)},
                label = { Text(text = "Title") },
            )
            TextField(
                value = homeUiState?.desc  ?: "",
                onValueChange = {viewModel?.OnChangeDesc(it)},
                label = { Text(text = "Description") },
            )
            TextField(
                value = homeUiState?.tag  ?: "",
                onValueChange = { viewModel?.OnChangeTag(it) },
                label = { Text(text = "Add a tag") },

                )


         //   Text(text = viewModel.representTags(viewModel.seperateTags()))
            Button(onClick = {
                if (!(homeUiState?.isUpdated ?: false)) {

                    viewModel?.OnAddTask()
                } else {
                    viewModel?.OnChangeSelectedTask(Task("", homeUiState?.title, homeUiState?.desc))
                    viewModel?.OnUpdateTask(homeUiState?.selectedTask?.documentId!!)
                }


                scope.launch {
                    viewModel?.OnChangeTitle("")
                    viewModel?.OnChangeDesc("")
                    viewModel?.OnChangeTag("")
                    sheetState.collapse()
                }
            }) {
                Text(text = if (!(homeUiState?.isUpdated  ?: false)) "Add Task" else "Update Task")
            }
        }

    }
}

@Composable
fun BottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    BottomNavigation(
        modifier = Modifier
            .padding(12.dp, 0.dp, 12.dp, 0.dp)
            .height(100.dp),
        //backgroundColor = Color.White,
        elevation = 0.dp
    ) {
        items.forEach {
            BottomNavigationItem(
                icon = {
                    it.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "",
                            modifier = Modifier.size(35.dp),
                            //tint = Color.Gray

                        )
                    }
                },
                label = {
                    it.title?.let {
                        Text(
                            text = it,
                            //color = Color.Gray
                        )
                    }
                },
                selected = currentRoute?.hierarchy?.any { it.route == it.route } == true,

                selectedContentColor = Color(R.color.purple_700),
                unselectedContentColor = Color.White.copy(alpha = 0.4f),
                onClick = {
                    it.route?.let { it1 ->
                        navController.navigate(it1) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview2() {

    HomeScreen(null
    )

}

