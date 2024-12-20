package com.example.todoappshmr.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoappshmr.data.Task
import com.example.todoappshmr.model.Importance
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ToDoItemRow(
    item: Task,
    onCheckedChange: (Boolean) -> Unit,
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = item.done,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
            },

            modifier =  Modifier.size(24.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = if (item.done) MaterialTheme.colorScheme.tertiary else Color.Gray,
                uncheckedColor = if (item.importance == Importance.IMPORTANT && !item.done) MaterialTheme.colorScheme.error else Color.Gray,

            )
        )


        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (item.importance == Importance.IMPORTANT && !item.done) {
                    "‼\uFE0F ${item.text}"
                }   else if (item.importance == Importance.LOW && !item.done){
                    "⬇${item.text}"
                }
                else {
                    item.text
                },
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
            )

            item.deadline?.let { deadline ->
                val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(deadline)
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Info",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
