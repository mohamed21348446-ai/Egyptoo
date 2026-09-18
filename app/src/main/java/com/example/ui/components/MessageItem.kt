package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MessageType
import com.example.games.GameType
import com.example.ui.theme.WhatsAppAudioWave
import com.example.ui.theme.WhatsAppBlueTick
import com.example.ui.theme.WhatsAppBubbleOther
import com.example.ui.theme.WhatsAppBubbleUser
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppSecurityGold
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(
    message: ChatMessageEntity,
    showEncryptedMode: Boolean,
    onLaunchGame: (GameType) -> Unit,
    onPlaySharedTrack: (String) -> Unit,
    onOpenSecurityVerification: () -> Unit,
    onInspectSender: ((name: String, avatar: String, gender: String, age: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (message.type == MessageType.SYSTEM_E2EE) {
        SystemE2EEMessage(
            message = message,
            onClick = onOpenSecurityVerification,
            modifier = modifier
        )
        return
    }

    val isFromMe = message.isFromMe
    val isRoyal = message.isOrnateRoyal || message.senderName.contains("معتز") || message.senderName.contains("👑")
    val isMale = message.senderGender.equals("MALE", ignoreCase = true)
    
    val bubbleColor = when {
        message.isGrandEntrance -> Color(0xFF2B1B0E)
        isRoyal -> Color(0xFF1F1A12)
        isFromMe -> WhatsAppBubbleUser
        else -> WhatsAppBubbleOther
    }
    val alignment = if (isFromMe) Alignment.End else Alignment.Start

    var showCiphertextForThisMessage by remember { mutableStateOf(false) }
    val displayEncrypted = showEncryptedMode || showCiphertextForThisMessage

    val timeString = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isFromMe) 14.dp else 2.dp,
                bottomEnd = if (isFromMe) 2.dp else 14.dp
            ),
            color = bubbleColor,
            border = if (isRoyal || message.isGrandEntrance || message.isGlobalBroadcast) {
                androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFFFFD54F))
                    )
                )
            } else null,
            tonalElevation = 2.dp,
            modifier = Modifier
                .widthIn(min = 100.dp, max = 320.dp)
                .testTag("chat_bubble_${message.id}")
        ) {
            Column(modifier = Modifier.padding(9.dp)) {
                // Royal / Broadcast Badge
                if (message.isGlobalBroadcast) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE65100).copy(alpha = 0.35f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🌐 برقية عامة لجميع الغرف والمجموعات 👑",
                            color = Color(0xFFFFD54F),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                } else if (message.isGrandEntrance) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF8F00).copy(alpha = 0.35f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚜️ مراسم دخول ملكي مهيب ⚜️",
                            color = Color(0xFFFFE082),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // SENDER PROFILE HEADER with Avatar, Name, Gender Badge, and Age Badge (واضح للكل)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            onInspectSender?.invoke(
                                message.senderName,
                                message.senderAvatar,
                                message.senderGender,
                                message.senderAge
                            )
                        }
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isRoyal) Color(0xFFFF8F00).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.senderAvatar.ifEmpty { if (isRoyal) "👑" else if (isMale) "👦" else "👧" },
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // Sender Name
                        Text(
                            text = message.senderName,
                            color = if (isRoyal) Color(0xFFFFD54F) else if (isFromMe) Color(0xFF81D4FA) else Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // GENDER & AGE BADGES (العمر واضح للكل وعلامة الجنس ولد/بنت)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // Gender Badge (ولد 👦 / بنت 👧)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isMale) Color(0xFF0D47A1).copy(alpha = 0.5f) else Color(0xFF880E4F).copy(alpha = 0.5f)
                                )
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (isMale) "ولد 👦" else "بنت 👧",
                                color = if (isMale) Color(0xFF80D8FF) else Color(0xFFFF80AB),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Age Badge (العمر)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${message.senderAge}س",
                                color = Color(0xFFEEEEEE),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                when (message.type) {
                    MessageType.TEXT -> {
                        if (displayEncrypted && message.encryptedPayload.isNotEmpty()) {
                            EncryptedPayloadBox(
                                payload = message.encryptedPayload,
                                onClick = { showCiphertextForThisMessage = !showCiphertextForThisMessage }
                            )
                        } else {
                            Text(
                                text = message.content,
                                color = WhatsAppTextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    MessageType.VOICE_NOTE -> {
                        VoiceNoteBubble(
                            durationSec = message.audioDurationSec,
                            isFromMe = isFromMe
                        )
                    }

                    MessageType.GAME_INVITE -> {
                        GameInviteBubble(
                            content = message.content,
                            gameType = when (message.gamePayload) {
                                "TRIVIA" -> GameType.TRIVIA
                                "SPIN_WHEEL" -> GameType.SPIN_WHEEL
                                "TIC_TAC_TOE" -> GameType.TIC_TAC_TOE
                                else -> GameType.TRIVIA
                            },
                            onLaunch = onLaunchGame
                        )
                    }

                    MessageType.GAME_RESULT -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏆", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.content,
                                color = WhatsAppTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    MessageType.MUSIC_TRACK -> {
                        MusicTrackShareBubble(
                            title = message.gamePayload.ifEmpty { message.content },
                            onPlay = { onPlaySharedTrack(message.gamePayload) }
                        )
                    }

                    MessageType.SYSTEM_E2EE -> {}
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Timestamp & Status ticks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.encryptedPayload.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "مشفر E2EE",
                            tint = WhatsAppTextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(10.dp)
                                .clickable {
                                    showCiphertextForThisMessage = !showCiphertextForThisMessage
                                }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }

                    Text(
                        text = timeString,
                        color = WhatsAppTextSecondary,
                        fontSize = 10.sp
                    )

                    if (isFromMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "تم القراءة",
                            tint = WhatsAppBlueTick,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemE2EEMessage(
    message: ChatMessageEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("e2ee_security_banner"),
        shape = RoundedCornerShape(10.dp),
        color = WhatsAppSurfaceVariant.copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, WhatsAppSecurityGold.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = WhatsAppSecurityGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = message.content,
                    color = WhatsAppSecurityGold,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Text(
                    text = "انقر للتحقق من رمز الأمان ومطابقة المفاتيح 🔒",
                    color = WhatsAppTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EncryptedPayloadBox(payload: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(WhatsAppDarkBackground)
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppSecurityGold, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "AES-256 مشفر (انقر لفك التشفير):",
                color = WhatsAppSecurityGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = payload,
            color = WhatsAppAudioWave,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 2
        )
    }
}

@Composable
private fun VoiceNoteBubble(durationSec: Int, isFromMe: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isFromMe) WhatsAppEmerald else WhatsAppDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "تشغيل المقطع الصوتي",
                tint = if (isFromMe) Color.Black else WhatsAppTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Simulated Waveform Bars
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            val heights = listOf(6, 12, 18, 10, 22, 14, 8, 16, 20, 12, 6, 14, 18, 10, 8)
            heights.forEach { h ->
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isFromMe) WhatsAppTextPrimary else WhatsAppAudioWave)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "0:${durationSec.toString().padStart(2, '0')}",
            color = WhatsAppTextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun GameInviteBubble(
    content: String,
    gameType: GameType,
    onLaunch: (GameType) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = WhatsAppEmerald, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "تحدي جماعي تفاعلي",
                color = WhatsAppEmerald,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = content, color = WhatsAppTextPrimary, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onLaunch(gameType) },
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("العب وتحدى الآن 🎮", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MusicTrackShareBubble(
    title: String,
    onPlay: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WhatsAppDarkBackground)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(WhatsAppAudioWave),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "مشاركة أغنية جماعية 🎧", color = WhatsAppAudioWave, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = WhatsAppTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Button(
            onClick = onPlay,
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald),
            modifier = Modifier.height(30.dp)
        ) {
            Text("تشغيل", color = Color.Black, fontSize = 10.sp)
        }
    }
}
