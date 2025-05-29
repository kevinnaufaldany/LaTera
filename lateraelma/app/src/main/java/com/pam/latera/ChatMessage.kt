package com.pam.latera.data

enum class Sender { USER, BOT }

data class ChatMessage(
    val id: Int,
    val sender: Sender,
    val text: String,
    val isTyping: Boolean = false
)