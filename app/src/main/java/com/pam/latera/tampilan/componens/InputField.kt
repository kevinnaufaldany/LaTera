package com.pam.latera.tampilan.componens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputField(
    inputText: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = inputText,
            onValueChange = onValueChange,
            placeholder = { Text("Ketik pesan...") },
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(50),
            maxLines = 1
        )
        IconButton(
            onClick = { if (inputText.isNotBlank()) onSendClick() },
            enabled = inputText.isNotBlank()
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Kirim")
        }
    }
}
