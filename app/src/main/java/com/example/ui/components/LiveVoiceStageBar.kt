package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.LiveVoiceState
import com.example.audio.StageParticipant
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun LiveVoiceStageBar(
    voiceState: LiveVoiceState,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleRaiseHand: () -> Unit,
    onSendReaction: (String) -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!voiceState.isLiveActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("live_voice_stage_bar"),
        shape = RoundedCornerShape(16.dp),
        color = WhatsAppSurfaceVariant.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(WhatsAppEmerald.copy(alpha = 0.5f), Color.Transparent))
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header row with Live Badge and Expand toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بث صوتي مباشر (Live Stage)",
                        color = WhatsAppTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Badge(
                        containerColor = WhatsAppEmerald,
                        contentColor = Color.Black
                    ) {
                        Text("${voiceState.speakers.size + voiceState.listeners.size} متواجد", fontSize = 10.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick mic button directly on header
                    FilledTonalIconButton(
                        onClick = {
                            onRequestPermission()
                            onToggleMic()
                        },
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (voiceState.isMyMicMuted) Color.Red.copy(alpha = 0.2f) else WhatsAppEmerald.copy(alpha = 0.25f),
                            contentColor = if (voiceState.isMyMicMuted) Color.Red else WhatsAppGreen
                        )
                    ) {
                        Icon(
                            imageVector = if (voiceState.isMyMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "المايك",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "طي/توسيع",
                            tint = WhatsAppTextSecondary
                        )
                    }
                }
            }

            // Expanded stage content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "المتحدثون على المسرح 🎙️",
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Speakers Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(voiceState.speakers) { speaker ->
                            SpeakerAvatarItem(speaker = speaker, pulseScale = pulseScale)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Listeners preview & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Raise hand toggle
                        FilledTonalIconButton(
                            onClick = onToggleRaiseHand,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("raise_hand_button"),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (voiceState.didIRaiseHand) Color(0xFFFFD279).copy(alpha = 0.25f) else WhatsAppDarkBackground,
                                contentColor = if (voiceState.didIRaiseHand) Color(0xFFFFD279) else WhatsAppTextSecondary
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PanTool, contentDescription = "رفع اليد", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (voiceState.didIRaiseHand) "تم رفع اليد ✋" else "طلب تحدث",
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Floating Emoji Reactions row
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("🔥", "❤️", "👏", "🎵", "🚀").forEach { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(WhatsAppDarkBackground)
                                        .clickable { onSendReaction(emoji) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeakerAvatarItem(
    speaker: StageParticipant,
    pulseScale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(62.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Glowing border when speaking
            if (speaker.isSpeaking) {
                Box(
                    modifier = Modifier
                        .size(46.dp * pulseScale)
                        .clip(CircleShape)
                        .background(WhatsAppGreen.copy(alpha = 0.35f))
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(WhatsAppDarkBackground)
                    .border(
                        width = if (speaker.isSpeaking) 2.dp else 1.dp,
                        color = if (speaker.isSpeaking) WhatsAppGreen else WhatsAppEmerald.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = speaker.avatarInitials,
                    fontSize = 18.sp,
                    color = WhatsAppTextPrimary
                )
            }

            // Small mic icon badge
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(if (speaker.isMuted) Color.Red else WhatsAppGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (speaker.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = speaker.name,
            color = WhatsAppTextPrimary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
