package com.example.todoappshmr.data.datasource.Room

import com.example.todoappshmr.data.model.Task

class TaskLocalDataSource(private val database: AppDatabase) {
    suspend fun getAllTasks(): List<Task> {
        return database.taskDao().getAllTasks()
    }
    suspend fun insertTask(task: Task) {
        database.taskDao().insertTask(task)
    }
    suspend fun deleteTask(task: Task) {
        database.taskDao().deleteTask(task)
    }
    suspend fun updateTaskCompletion(taskId: Int, isCompleted: Boolean) {
        database.taskDao().updateTaskCompletion(taskId, isCompleted)
    }
    suspend fun updateTask(task: Task) {
        database.taskDao().updateTask(task)
    }
    suspend fun getTaskById(taskId: Int): Task? {
        return database.taskDao().getTaskById(taskId)
    }
    suspend fun getLastTaskId(): Int? {
        return database.taskDao().getLastTaskId()
    }


}