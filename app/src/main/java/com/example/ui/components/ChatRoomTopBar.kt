package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatRoomEntity
import com.example.ui.theme.WhatsAppAudioWave
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppSecurityGold
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun ChatRoomTopBar(
    room: ChatRoomEntity?,
    isVoiceBroadcastActive: Boolean,
    isMusicPlaying: Boolean,
    typingFriend: String?,
    onBackClick: () -> Unit,
    onOpenSecurityVerification: () -> Unit,
    onToggleVoiceBroadcast: () -> Unit,
    onOpenMusicQueue: () -> Unit,
    onOpenGamesPicker: () -> Unit,
    isSuperAdmin: Boolean = false,
    onTriggerGrandEntrance: (() -> Unit)? = null,
    onOpenProfile: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("chat_room_top_bar"),
        color = WhatsAppSurfaceVariant,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(38.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = WhatsAppTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(WhatsAppDarkBackground)
                        .clickable { onOpenSecurityVerification() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = room?.avatarInitials ?: "🎧",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenSecurityVerification() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = room?.name ?: "محادثة الأصدقاء",
                            color = WhatsAppTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "مشفر تماماً",
                            tint = WhatsAppSecurityGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    val subtitleText = when {
                        typingFriend != null -> "$typingFriend يكتب الآن..."
                        isVoiceBroadcastActive -> "🎙️ بث صوتي مباشر نشط"
                        room?.isGroup == true -> "${room.memberCount} أعضاء • مشفر E2EE"
                        else -> "متصل الآن • تشفير تام 256-bit"
                    }

                    Text(
                        text = subtitleText,
                        color = if (typingFriend != null || isVoiceBroadcastActive) WhatsAppGreen else WhatsAppTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick Action Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSuperAdmin && onTriggerGrandEntrance != null) {
                    IconButton(
                        onClick = onTriggerGrandEntrance,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                    }
                }

                if (onOpenProfile != null) {
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(text = "👤", fontSize = 18.sp)
                    }
                }

                // Voice Stage Toggle Button
                IconButton(
                    onClick = onToggleVoiceBroadcast,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "البث الصوتي المباشر",
                        tint = if (isVoiceBroadcastActive) WhatsAppGreen else WhatsAppTextSecondary
                    )
                }

                // Music Queue Button
                IconButton(
                    onClick = onOpenMusicQueue,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "الموسيقى الجماعية",
                        tint = if (isMusicPlaying) WhatsAppAudioWave else WhatsAppTextSecondary
                    )
                }

                // Games Launcher Button
                IconButton(
                    onClick = onOpenGamesPicker,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "التحديات والألعاب",
                        tint = WhatsAppEmerald
                    )
                }
            }
        }
    }
}
