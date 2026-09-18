package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.audio.MusicPlayerState
import com.example.ui.theme.WhatsAppAudioWave
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun SharedMusicPlayerBar(
    musicState: MusicPlayerState,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onOpenQueue: () -> Unit,
    onShareTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = musicState.currentTrack

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("shared_music_player_bar"),
        shape = RoundedCornerShape(16.dp),
        color = WhatsAppSurfaceVariant.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(WhatsAppAudioWave.copy(alpha = 0.4f), WhatsAppEmerald.copy(alpha = 0.2f))
            )
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(WhatsAppAudioWave, WhatsAppEmerald))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "موسيقى جماعية 🎧",
                                color = WhatsAppAudioWave,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(
                                containerColor = WhatsAppEmerald.copy(alpha = 0.2f),
                                contentColor = WhatsAppGreen
                            ) {
                                Text("${musicState.activeListeners} يستمعون", fontSize = 9.sp)
                            }
                        }
                        Text(
                            text = track.title,
                            color = WhatsAppTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Compact Controls on the right
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = WhatsAppEmerald,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (musicState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "تشغيل/إيقاف",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "توسيع",
                            tint = WhatsAppTextSecondary
                        )
                    }
                }
            }

            // Expanded detail: Equalizer, progress bar, queue & share
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Audio Equalizer Frequency Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WhatsAppDarkBackground.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        musicState.visualizerAmplitudes.forEach { amp ->
                            val barHeight = (20.dp * amp).coerceAtLeast(3.dp)
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(barHeight)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(WhatsAppAudioWave, WhatsAppEmerald)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    val progress = if (track.durationSec > 0) {
                        (musicState.currentPositionSec / track.durationSec).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = WhatsAppAudioWave,
                        trackColor = WhatsAppDarkBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Full Playback Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Queue and Share buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalIconButton(
                                onClick = onOpenQueue,
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = WhatsAppDarkBackground,
                                    contentColor = WhatsAppTextPrimary
                                )
                            ) {
                                Icon(Icons.Default.QueueMusic, contentDescription = "قائمة الأغاني", modifier = Modifier.size(16.dp))
                            }

                            FilledTonalIconButton(
                                onClick = onShareTrack,
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = WhatsAppDarkBackground,
                                    contentColor = WhatsAppEmerald
                                )
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "مشاركة في المحادثة", modifier = Modifier.size(16.dp))
                            }
                        }

                        // Prev / Play / Next Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = onPrev, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "السابق", tint = WhatsAppTextPrimary)
                            }
                            FilledTonalIconButton(
                                onClick = onPlayPause,
                                modifier = Modifier.size(38.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = WhatsAppEmerald,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    imageVector = if (musicState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "تشغيل",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            IconButton(onClick = onNext, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.SkipNext, contentDescription = "التالي", tint = WhatsAppTextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
