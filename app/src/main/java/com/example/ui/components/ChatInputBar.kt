package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppAudioWave
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSendVoiceNote: () -> Unit,
    onOpenGamesPicker: () -> Unit,
    onOpenMusicQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main input container
        Surface(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(24.dp),
            color = WhatsAppSurfaceVariant,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Games Trigger Button
                IconButton(
                    onClick = onOpenGamesPicker,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("launch_games_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "بدء تحدي أو لعبة",
                        tint = WhatsAppEmerald
                    )
                }

                // Music Queue Button
                IconButton(
                    onClick = onOpenMusicQueue,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "الموسيقى المشتركة",
                        tint = WhatsAppAudioWave
                    )
                }

                // Text Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "اكتب رسالة مشفرة...",
                            color = WhatsAppTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = onInputTextChanged,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = WhatsAppTextPrimary,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(WhatsAppEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_input_text_field"),
                        maxLines = 4
                    )
                }

                // Emoji Icon
                Icon(
                    imageVector = Icons.Default.SentimentSatisfiedAlt,
                    contentDescription = "رموز تعبيرية",
                    tint = WhatsAppTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Send or Voice Note FAB
        FloatingActionButton(
            onClick = {
                if (inputText.isNotBlank()) {
                    onSendMessage()
                } else {
                    onSendVoiceNote()
                }
            },
            modifier = Modifier
                .size(48.dp)
                .testTag("send_or_voice_fab"),
            shape = CircleShape,
            containerColor = WhatsAppEmerald,
            contentColor = Color.Black,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
        ) {
            if (inputText.isNotBlank()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "تسجيل مقطع صوتي",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
