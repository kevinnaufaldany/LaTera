package com.pam.latera

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pam.latera.data.ChatMessage
import com.pam.latera.data.Sender
import com.pam.latera.logic.PredictBot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.isBlank
import kotlin.text.isNotBlank
import kotlin.text.repeat
import kotlin.text.trim

private val quickQuestions = listOf(
    "Di mana Lab IoT?",
    "Dimana GK2?",
    "Letak Gedung A?",
    "Lokasi Embung A di mana??",
    "Posisi GSG Itera?"
)

@Composable
fun ChatBotScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        PredictBot.init(context)
    }

    ChatScreen(quickQuestions, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(quickQuestions: List<String>, onBack: () -> Unit) {
    val context = LocalContext.current
    val popSound = remember {
        MediaPlayer.create(context, R.raw.pop)
    }

    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var messages by remember {
        mutableStateOf<List<ChatMessage>>(
            listOf(
                ChatMessage(
                    id = 0,
                    sender = Sender.BOT,
                    text = "Halo! Selamat datang di NavBot. Ada yang bisa saya bantu?"
                )
            )
        )
    }
    var isBotTyping by remember { mutableStateOf(false) }

    fun addUserMessage(text: String) {
        if (text.isBlank()) return
        messages = messages + ChatMessage(id = messages.size, sender = Sender.USER, text = text)
        inputText = TextFieldValue("")
    }

    fun addBotTyping() {
        messages = messages + ChatMessage(id = messages.size, sender = Sender.BOT, text = "", isTyping = true)
        isBotTyping = true
    }

    fun removeBotTyping() {
        messages = messages.filterNot { it.isTyping }
        isBotTyping = false
    }

    fun addBotMessage(text: String) {
        messages = messages + ChatMessage(id = messages.size, sender = Sender.BOT, text = text)
        popSound.start()
    }

    fun botReply(userInput: String) {
        coroutineScope.launch {
            addBotTyping()
            delay(100)
            scrollState.animateScrollTo(scrollState.maxValue)

            PredictBot.predict(userInput) { reply ->
                coroutineScope.launch {
                    removeBotTyping()
                    addBotMessage(reply)
                    delay(100)
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }
        }
    }

    Scaffold(
        topBar = { Header(onBack = onBack) },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFE3F8FD)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                for (message in messages) {
                    ChatBubble(message = message)
                }
            }

            QuickQuestions(
                questions = quickQuestions,
                onQuestionClick = { question ->
                    addUserMessage(question)
                    botReply(question)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            InputFieldWithIcon(
                inputText = inputText,
                onValueChange = { inputText = it },
                onSendClick = {
                    val text = inputText.text.trim()
                    if (text.isNotBlank()) {
                        addUserMessage(text)
                        botReply(text)
                    }
                }
            )
        }
    }
}

@Composable
fun Header(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF80B9FF))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(28.dp)
                .clickable { onBack() }
        )
        Text(
            text = "NavBot",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun QuickQuestions(
    questions: List<String>,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        for (question in questions) {
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onQuestionClick(question) },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF4698D2)
            ) {
                Text(
                    text = question,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun InputFieldWithIcon(
    inputText: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = inputText,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSendClick,
            modifier = Modifier
                .size(48.dp)
                .background(color = Color(0xFF89C6FA), shape = RoundedCornerShape(20.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val maxBubbleWidth = 280.dp
    val isUser = message.sender == Sender.USER

    val bubbleColor = if (isUser) Color(0xFF89C6FA) else Color(0xFFDAF1FF)
    val align = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = align
    ) {
        if (message.isTyping) {
            BotTypingBubble(maxBubbleWidth)
        } else {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.widthIn(max = maxBubbleWidth)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BotTypingBubble(maxWidth: Dp) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedDotCount by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val dots = ".".repeat(animatedDotCount)

    Surface(
        color = Color(0xFFE1F5FE),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.widthIn(max = maxWidth)
    ) {
        Text(
            text = "Bot sedang mengetik$dots",
            modifier = Modifier.padding(12.dp),
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}
