package com.example.todoappshmr

import AppTheme
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.todoappshmr.navigation.AppNavigation
import com.example.todoappshmr.repository.TodoItemsRepository

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TodoItemsRepository()
        setContent {
            AppTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}

