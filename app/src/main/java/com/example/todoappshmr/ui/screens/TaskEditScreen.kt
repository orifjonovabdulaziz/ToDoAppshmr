package com.example.todoappshmr.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoappshmr.data.Task
import com.example.todoappshmr.repository.TaskViewModel
import com.example.todoappshmr.utils.getImportanceFromString
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    viewModel: TaskViewModel = viewModel(),
    initialTaskDescription: String = "",
    initialDeadline: Date? = null,
    initialPriority: String = "Нет",
    onSave: (Task) -> Unit,
    onDelete: (Int) -> Unit,
    onClose: () -> Unit,
    taskId: Int? = null
) {
    var taskDescription by remember { mutableStateOf(TextFieldValue(initialTaskDescription)) }
    var selectedDeadline by remember { mutableStateOf(initialDeadline) }
    var selectedPriority by remember { mutableStateOf(initialPriority) }

    val priorities = listOf("Нет", "Низкий", "Высокий")
    var isDropdownExpanded by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (taskId == null) "Добавить задание" else "Редактировать задание") },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Поле ввода описания
            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Описание задания") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка выбора даты
            Button(
                onClick = {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(selectedDeadline?.time ?: MaterialDatePicker.todayInUtcMilliseconds())
                        .build()

                    datePicker.addOnPositiveButtonClickListener { dateInMillis ->
                        selectedDeadline = Date(dateInMillis)
                    }

                    datePicker.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "datePicker")
                }
            ) {
                Text(text = "Выбрать дату дедлайна")
            }

            selectedDeadline?.let {
                Text(text = "Дедлайн: ${dateFormatter.format(it)}", modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DropdownMenu для выбора приоритета
            Box {
                Text(
                    text = "Приоритет: $selectedPriority",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDropdownExpanded = true }
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                selectedPriority = priority
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            val importance = getImportanceFromString(selectedPriority)

            // Кнопки управления
            Row {
                Button(
                    onClick = {
                        val newTask = Task(
                            id = taskId ?: 0, // Используем существующий ID или создаем новый
                            text = taskDescription.text,
                            importance = importance,
                            deadline = selectedDeadline,
                            done = false,
                            created_at = Date(),
                            changed_at = Date()
                        )

                        if (taskId == null) {
                            // Добавление новой задачи
                            viewModel.addTask(newTask)
                        } else {
                            // Обновление существующей задачи
                            viewModel.updateTask(newTask)
                        }
                        onClose()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сохранить")
                }

                if (taskId != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onDelete(taskId)
                            viewModel.removeTask(taskId)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }
}
