package com.example.todoappshmr.repository

import com.example.todoappshmr.model.Importance
import com.example.todoappshmr.model.ToDoItem
import java.util.Date

class TodoItemsRepository {

    private var currentId = 0


    private val items: MutableList<ToDoItem> = mutableListOf(
        *Array(20) { i ->
            ToDoItem(
                id = currentId++,
                text = "Task #$i",
                importance = Importance.values().random(),
                deadline = if (i % 2 == 0) Date() else null,
                isCompleted = i % 3 == 0,
                createdAt = Date(),
                modifiedAt = Date()
            )
        }
    )

    fun getTodoItems(): List<ToDoItem> {
        return items
    }

    fun addTodoItem(item: ToDoItem) {
        items.add(item.copy(id = currentId++))
    }

    fun removeTodoItemById(id: Int): Boolean {
        return items.removeIf { it.id == id }
    }

    fun updateItemCompletion(id: Int, isCompleted: Boolean) {
        items.find { it.id == id }?.let { item ->
            val index = items.indexOf(item)
            items[index] = item.copy(isCompleted = isCompleted)
            println("Item updated: ${item.id} to isCompleted = $isCompleted")
        }
    }


}
