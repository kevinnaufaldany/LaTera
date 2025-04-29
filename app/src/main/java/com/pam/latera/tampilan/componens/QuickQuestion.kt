package com.pam.latera.tampilan.componens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickQuestion(questions: List<String>, onQuestionClicked: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        questions.forEach { question ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clickable { onQuestionClicked(question) }
            ) {
                Text(text = question, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
