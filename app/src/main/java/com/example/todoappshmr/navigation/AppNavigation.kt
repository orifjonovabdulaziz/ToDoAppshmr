package com.example.todoappshmr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoappshmr.ui.screens.MainScreen
import com.example.todoappshmr.ui.screens.TaskEditScreen
import com.example.todoappshmr.repository.TaskViewModel

@Composable
fun AppNavigation(viewModel: TaskViewModel) {
    val navController = rememberNavController()
//    val viewModel: TaskViewModel = viewModel()


    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            MainScreen(viewModel = viewModel,
                onAddTaskClicked = { navController.navigate("taskEdit") },
                onTaskClicked = { taskId ->
                    navController.navigate("taskEdit/$taskId")
                }
            )
        }
        composable("taskEdit") {
            TaskEditScreen(viewModel = viewModel,
                onSave = { task -> viewModel.addTask(task) },
                onDelete = { id -> viewModel.removeTask(id) },
                onClose = { navController.popBackStack() },

            )
        }
        composable("taskEdit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val task = taskId?.let { id -> viewModel.tasks.value.find { it.id == id } }


            if (task != null) {
                TaskEditScreen(
                    viewModel = viewModel,
                    onSave = { task -> viewModel.addTask(task) },
                    onDelete = { id -> viewModel.removeTask(id) },
                    onClose = { navController.popBackStack() },
                    taskId = taskId,
                    initialTaskDescription = task.text,
                    initialDeadline = task.deadline,
                    initialPriority = task.importance.value,

                )
            }
        }

        composable("taskEdit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val task = taskId?.let { viewModel.tasks.value.find { it.id == taskId } }

            task?.let {
                TaskEditScreen(
                    initialTaskDescription = it.text,
                    initialDeadline = it.deadline,
                    initialPriority = it.importance.value,
                    viewModel = viewModel,
                    onSave = { task -> viewModel.addTask(task) },
                    onDelete = { id -> viewModel.removeTask(id) },
                    onClose = { navController.popBackStack() },
                    taskId = taskId,

                )
            }
        }
    }
}
