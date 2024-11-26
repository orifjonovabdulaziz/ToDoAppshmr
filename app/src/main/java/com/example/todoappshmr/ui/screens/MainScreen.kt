package com.example.todoappshmr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoappshmr.ui.ToDoItemRow
import com.example.todoappshmr.ui.stateholders.TaskViewModel
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddTaskClicked: () -> Unit,
    onTaskClicked: (Int) -> Unit = {},
    viewModel: TaskViewModel
) {

    val tasks by viewModel.tasks.collectAsState()
    val completedTasksCount by remember { derivedStateOf { tasks.count { it.done } } }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showSecondTitle by remember { mutableStateOf(true) }

    var showCompleted by remember { mutableStateOf(true) }


    LaunchedEffect(scrollBehavior.state.collapsedFraction) {
        showSecondTitle = scrollBehavior.state.collapsedFraction < 0.5f
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = "Мои дела", color = MaterialTheme.colorScheme.tertiaryContainer,
                            fontSize = 32.sp,
                            )

                        if (showSecondTitle){
                            Text(
                                text = "Выполнено - $completedTasksCount",
                                style = MaterialTheme.typography.headlineMedium,
                                fontSize = 16.sp,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showCompleted = !showCompleted }) {
                        Icon(
                            imageVector = if (showCompleted) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showCompleted) "Скрыть выполненные" else "Показать выполненные"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.Black,
                ),
                scrollBehavior = scrollBehavior

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClicked,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задание")
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(3.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            val filteredTasks = if (showCompleted) tasks else tasks.filter { !it.done }

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                items(filteredTasks, key = { it.id }) { item ->
                    ToDoItemRow(
                        item = item,
                        onCheckedChange = { isChecked ->
                            viewModel.toggleTaskCompletion(item.id)
                        },
                        onItemClick = {
                            onTaskClicked(item.id)
                        }
                    )
                }
            }
        }
    }
}
