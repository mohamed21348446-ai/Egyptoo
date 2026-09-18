package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MusicPlayerState
import com.example.audio.TrackItem
import com.example.ui.theme.WhatsAppAudioWave
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun MusicQueueDialog(
    playerState: MusicPlayerState,
    onSelectTrack: (TrackItem) -> Unit,
    onAddTrack: (title: String, artist: String) -> Unit,
    onUpvoteTrack: (trackId: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newSongTitle by remember { mutableStateOf("") }
    var newSongArtist by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("music_queue_dialog"),
        containerColor = WhatsAppSurfaceVariant,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = null,
                        tint = WhatsAppAudioWave,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "طابور الأغاني المشترك 🎧",
                        color = WhatsAppTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = WhatsAppTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "الأغاني يتم تشغيلها بالتزامن مع جميع أعضاء الغرفة. يمكنك التصويت أو اقتراح أغنية جديدة:",
                    color = WhatsAppTextSecondary,
                    fontSize = 12.sp
                )

                // Add song input
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WhatsAppDarkBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "اقتراح أغنية جديدة:",
                            color = WhatsAppEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newSongTitle,
                            onValueChange = { newSongTitle = it },
                            placeholder = { Text("اسم الأغنية أو اللحن...", fontSize = 11.sp, color = WhatsAppTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = WhatsAppTextPrimary,
                                unfocusedTextColor = WhatsAppTextPrimary,
                                focusedBorderColor = WhatsAppEmerald,
                                unfocusedBorderColor = WhatsAppTextSecondary.copy(alpha = 0.3f)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = newSongArtist,
                                onValueChange = { newSongArtist = it },
                                placeholder = { Text("اسم الفنان / الملحن", fontSize = 11.sp, color = WhatsAppTextSecondary) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = WhatsAppTextPrimary,
                                    unfocusedTextColor = WhatsAppTextPrimary,
                                    focusedBorderColor = WhatsAppEmerald,
                                    unfocusedBorderColor = WhatsAppTextSecondary.copy(alpha = 0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (newSongTitle.isNotBlank()) {
                                        onAddTrack(
                                            newSongTitle.trim(),
                                            newSongArtist.ifBlank { "طلب الأصدقاء" }.trim()
                                        )
                                        newSongTitle = ""
                                        newSongArtist = ""
                                    }
                                },
                                enabled = newSongTitle.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("إضافة", color = Color.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Playlist items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(playerState.playlist) { track ->
                        val isCurrent = track.id == playerState.currentTrack.id
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) WhatsAppEmerald.copy(alpha = 0.15f) else WhatsAppDarkBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCurrent) WhatsAppEmerald else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (isCurrent && playerState.isPlaying) {
                                        Icon(
                                            Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = WhatsAppAudioWave,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(WhatsAppTextSecondary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🎵", fontSize = 10.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = track.title,
                                            color = if (isCurrent) WhatsAppEmerald else WhatsAppTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${track.artist} • أضافها: ${track.addedBy}",
                                            color = WhatsAppTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilledTonalIconButton(
                                        onClick = { onUpvoteTrack(track.id) },
                                        modifier = Modifier.size(32.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = WhatsAppDarkBackground,
                                            contentColor = WhatsAppAudioWave
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ThumbUp, contentDescription = "تصويت", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("${track.votes}", fontSize = 10.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Button(
                                        onClick = { onSelectTrack(track) },
                                        enabled = !isCurrent,
                                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(if (isCurrent) "الحالية" else "تشغيل", fontSize = 10.sp, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
