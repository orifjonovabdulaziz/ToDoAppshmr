package com.example.todoappshmr.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import com.example.todoappshmr.data.Task
import com.example.todoappshmr.model.ToDoItem
import com.example.todoappshmr.ui.ToDoItemRow
import kotlin.math.roundToInt

@Composable
fun SwipeableTaskItem(
    item: Task,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
) {
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > with(density) { 100.dp.toPx() }) {
                            onComplete()
                        } else if (offsetX < -with(density) { 100.dp.toPx() }) {
                            onDelete()
                        }
                        offsetX = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount
                }
            }
            .offset { IntOffset(offsetX.roundToInt(), 0) }
    ) {
        when {
            offsetX > 0 -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выполнено",
                    tint = Color.Green,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            offsetX < 0 -> {
                
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }


        ToDoItemRow(
            item = item,
            onCheckedChange = { isChecked ->
                if (isChecked) onComplete()
            },
            onItemClick = { }
        )
    }
}
