package com.example.todoappshmr.utils


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoappshmr.model.Importance

@Composable
fun PrioritySelector(
    currentPriority: Importance,
    onPriorityChange: (Importance) -> Unit
) {
    val priorities = listOf(
        Importance.LOW to "⬇",
        Importance.BASIC to "нет",
        Importance.IMPORTANT to "‼"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Важность",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(end = 8.dp)
        )

        Row(
            modifier = Modifier
                .background(Color.LightGray, RoundedCornerShape(16.dp))
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            priorities.forEach { (priority, label) ->
                val isSelected = priority == currentPriority
                Text(
                    text = label,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onPriorityChange(priority) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
