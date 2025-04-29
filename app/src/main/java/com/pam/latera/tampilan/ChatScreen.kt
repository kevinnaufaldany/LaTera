package com.pam.latera.tampilan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pam.latera.data.ChatMessage
import com.pam.latera.model.PredictBot
import com.pam.latera.tampilan.componens.ChatBubble
import com.pam.latera.tampilan.componens.InputField
import com.pam.latera.tampilan.componens.QuickQuestion
import com.pam.latera.tampilan.componens.TopBar
import com.pam.latera.utils.FirestoreHelper
import com.pam.latera.utils.SoundPlayer

@Composable
fun ChatScreen(
    predictBot: PredictBot,
    firestoreHelper: FirestoreHelper,
    soundPlayer: SoundPlayer
) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    val quickQuestions = listOf(
        "Di mana gedung B?",
        "Apa fasilitas kampus?",
        "Jadwal kuliah?",
        "Siapa kamu?"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "NavBot Kampus", onBackPressed = {})

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message.message, message.isUser)
            }
        }

        QuickQuestion(questions = quickQuestions) { question ->
            inputText = question
        }

        InputField(
            inputText = inputText,
            onValueChange = { inputText = it },
            onSendClick = {
                if (inputText.isNotBlank()) {
                    val userMessage = ChatMessage(inputText, isUser = true)
                    messages = messages + userMessage
                    firestoreHelper.saveMessage(userMessage)

                    val botReply = predictBot.predict(userMessage.message)
                    val botMessage = ChatMessage(botReply, isUser = false)
                    messages = messages + botMessage
                    firestoreHelper.saveMessage(botMessage)

                    soundPlayer.playSendSound()
                    inputText = ""
                }
            }
        )
    }
}
