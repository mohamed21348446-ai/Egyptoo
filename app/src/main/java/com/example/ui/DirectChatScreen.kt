package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DirectMessage
import com.example.data.model.FriendUser
import com.example.data.model.MessageType
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooGreen
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary

@Composable
fun DirectChatScreen(
    friend: FriendUser,
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.directMessages.collectAsState()
    val isFriendTyping by viewModel.isDirectFriendTyping.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    var isCallActive by remember { mutableStateOf(false) }
    var callSeconds by remember { mutableStateOf(0) }
    var showEmojiStickers by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(isCallActive) {
        if (isCallActive) {
            while (isCallActive) {
                kotlinx.coroutines.delay(1000)
                callSeconds++
            }
        } else {
            callSeconds = 0
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("direct_chat_screen"),
        containerColor = EgyptooDarkBg,
        topBar = {
            DirectChatTopBar(
                friend = friend,
                onNavigateBack = onNavigateBack,
                onStartCall = { isCallActive = true }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(EgyptooDarkCard)) {
                if (showEmojiStickers) {
                    EmojiStickersBar(
                        onSelectEmoji = { emoji ->
                            viewModel.sendDirectMediaEmoji(friend.id, emoji)
                            showEmojiStickers = false
                        }
                    )
                }

                DirectChatBottomBar(
                    inputText = inputMessage,
                    onTextChanged = { inputMessage = it },
                    onSend = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendDirectMessage(friend.id, inputMessage)
                            inputMessage = ""
                        }
                    },
                    onToggleStickers = { showEmojiStickers = !showEmojiStickers },
                    onSendVoice = {
                        viewModel.sendDirectVoiceNote(friend.id)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // E2EE Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = EgyptooDarkCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = EgyptooGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "محادثة خاصة سرية ومشفرة تماماً E2EE بينك وبين ${friend.name} 🔒",
                        color = EgyptooGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val friendMessages = messages.filter {
                    (it.senderId == friend.id) || (it.receiverId == friend.id)
                }

                items(friendMessages, key = { it.id }) { msg ->
                    val isMe = msg.senderId != friend.id
                    DirectMessageBubble(message = msg, isMe = isMe)
                }

                if (isFriendTyping) {
                    item {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = friend.avatar, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${friend.name} يكتب الآن... ✍️",
                                color = EgyptooSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Direct Voice Call Dialog Simulation
    if (isCallActive) {
        Dialog(onDismissRequest = { isCallActive = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = EgyptooDarkBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, EgyptooGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "مكالمة صوتية خاصة مشفرة 📞",
                        color = EgyptooGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(EgyptooGreen.copy(alpha = 0.4f), EgyptooDarkCard)
                                )
                            )
                            .border(2.dp, EgyptooGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = friend.avatar, fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = friend.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = friend.countryFlag, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    val min = callSeconds / 60
                    val sec = callSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", min, sec),
                        color = EgyptooSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "الصوت نقي ومباشر عبر خوادم Egyptoo 🎙️",
                        color = EgyptooTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    Button(
                        onClick = { isCallActive = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "إنهاء المكالمة", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectChatTopBar(
    friend: FriendUser,
    onNavigateBack: () -> Unit,
    onStartCall: () -> Unit
) {
    Surface(
        color = EgyptooDarkCard,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White
                    )
                }

                Box {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EgyptooPrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = friend.avatar, fontSize = 20.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (friend.isOnline) EgyptooGreen else EgyptooTextSecondary)
                            .border(1.dp, EgyptooDarkCard, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = friend.name,
                            color = EgyptooTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = friend.countryFlag, fontSize = 12.sp)
                    }
                    Text(
                        text = if (friend.isOnline) "متصل الآن 🟢" else "غير متصل ⚪",
                        color = if (friend.isOnline) EgyptooGreen else EgyptooTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Voice Call Action
            IconButton(
                onClick = onStartCall,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(EgyptooGreen.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Call, contentDescription = "اتصال صوتي خاص", tint = EgyptooGreen, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun DirectMessageBubble(
    message: DirectMessage,
    isMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) EgyptooPrimary else EgyptooDarkCard,
            border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (message.type == MessageType.VOICE_NOTE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = if (isMe) Color.White else EgyptooSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مقطع صوتي خاص (${message.audioDuration} ثانية)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (message.mediaEmoji != null) {
                    Text(text = message.mediaEmoji, fontSize = 42.sp)
                } else {
                    Text(
                        text = message.content,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مشفر 🔒",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectChatBottomBar(
    inputText: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onToggleStickers: () -> Unit,
    onSendVoice: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggleStickers, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Mood, contentDescription = "ملصقات", tint = EgyptooSecondary)
        }

        IconButton(onClick = onSendVoice, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Mic, contentDescription = "تسجيل صوتي", tint = EgyptooCyan)
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = onTextChanged,
            placeholder = { Text("رسالة خاصة مشفرة...", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = EgyptooTextPrimary,
                unfocusedTextColor = EgyptooTextPrimary,
                focusedBorderColor = EgyptooPrimary,
                unfocusedBorderColor = EgyptooBorder
            )
        )

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(EgyptooPrimary)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "إرسال", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EmojiStickersBar(
    onSelectEmoji: (String) -> Unit
) {
    val emojis = listOf("🔥", "❤️", "😂", "👑", "🚀", "🎮", "👏", "💎", "⭐", "🎉")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        emojis.forEach { emoji ->
            Text(
                text = emoji,
                fontSize = 26.sp,
                modifier = Modifier
                    .clickable { onSelectEmoji(emoji) }
                    .padding(4.dp)
            )
        }
    }
}
