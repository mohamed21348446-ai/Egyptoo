package com.example.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.FloatingReaction
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ChatRoomTopBar
import com.example.ui.components.E2EESecurityDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.GamesPickerDialog
import com.example.ui.components.GrandEntranceOverlay
import com.example.ui.components.InChatGameWidget
import com.example.ui.components.LiveVoiceStageBar
import com.example.ui.components.MessageItem
import com.example.ui.components.MusicQueueDialog
import com.example.ui.components.PrankArsenalDialog
import com.example.ui.components.PranksVisualEffectsOverlay
import com.example.ui.components.SharedMusicPlayerBar
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooGreen
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary

@Composable
fun ChatRoomScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentRoom by viewModel.currentRoom.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val musicState by viewModel.musicPlayer.playerState.collectAsState()
    val voiceState by viewModel.voiceBroadcast.voiceState.collectAsState()
    val gameState by viewModel.gamesManager.gameState.collectAsState()

    val isMoatazSuperAdmin = viewModel.isSuperAdmin()
    val listState = rememberLazyListState()

    // Permission launcher for Live Voice broadcast microphone
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.voiceBroadcast.startMicRecordingIfPermitted()
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Disco strobe animation
    val infiniteTransition = rememberInfiniteTransition(label = "disco")
    val discoColorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "discoPhase"
    )

    // Earthquake violent screen shake animation
    val earthquakeTransition = rememberInfiniteTransition(label = "earthquake")
    val earthquakeX by earthquakeTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "earthquakeX"
    )
    val earthquakeY by earthquakeTransition.animateFloat(
        initialValue = 6f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(65, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "earthquakeY"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_room_screen"),
        topBar = {
            ChatRoomTopBar(
                room = currentRoom,
                isVoiceBroadcastActive = voiceState.isLiveActive,
                isMusicPlaying = musicState.isPlaying,
                typingFriend = uiState.activeFriendTyping,
                onBackClick = onNavigateBack,
                onOpenSecurityVerification = { viewModel.setShowSecurityDialog(true) },
                onToggleVoiceBroadcast = { viewModel.voiceBroadcast.toggleLiveBroadcast() },
                onOpenMusicQueue = { viewModel.setShowMusicQueue(true) },
                onOpenGamesPicker = { viewModel.setShowGamesPicker(true) },
                isSuperAdmin = isMoatazSuperAdmin,
                onTriggerGrandEntrance = { viewModel.triggerGrandEntrance() },
                onOpenProfile = { viewModel.setShowEditProfileDialog(true) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                ChatInputBar(
                    inputText = uiState.inputText,
                    onInputTextChanged = { viewModel.onInputTextChanged(it) },
                    onSendMessage = { viewModel.sendMessage() },
                    onSendVoiceNote = { viewModel.sendVoiceNote() },
                    onOpenGamesPicker = { viewModel.setShowGamesPicker(true) },
                    onOpenMusicQueue = { viewModel.setShowMusicQueue(true) }
                )
            }
        },
        floatingActionButton = {
            if (isMoatazSuperAdmin) {
                FloatingActionButton(
                    onClick = { viewModel.setShowPrankArsenalDialog(true) },
                    containerColor = EgyptooSecondary,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("moataz_prank_arsenal_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪄👑", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مقالب معتز",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        containerColor = EgyptooDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .offset(
                    x = if (uiState.isEarthquakeActive) earthquakeX.dp else 0.dp,
                    y = if (uiState.isEarthquakeActive) earthquakeY.dp else 0.dp
                )
                .background(
                    if (uiState.isDiscoPrankActive) {
                        Brush.verticalGradient(
                            listOf(
                                if (discoColorPhase > 0.5f) EgyptooPrimary.copy(alpha = 0.35f) else EgyptooSecondary.copy(alpha = 0.35f),
                                if (discoColorPhase > 0.5f) EgyptooPurple.copy(alpha = 0.35f) else EgyptooCyan.copy(alpha = 0.35f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                EgyptooDarkBg,
                                EgyptooDarkCard.copy(alpha = 0.5f),
                                EgyptooDarkBg
                            )
                        )
                    }
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Upside-Down Prank Banner Notice
                if (uiState.isUpsideDownPrankActive) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFF9100).copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9100))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = "🙃", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مقلب الجاذبية المعكوسة! الشاشة مقلوبة 180° بواسطة المشرف معتز 🤣",
                                    color = Color(0xFFFF9100),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (isMoatazSuperAdmin) {
                                IconButton(
                                    onClick = { viewModel.toggleUpsideDownPrank() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Undo, contentDescription = "استعادة", tint = Color(0xFFFF9100))
                                }
                            }
                        }
                    }
                }

                // Cat language banner
                if (uiState.isCatLanguagePrankActive) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = EgyptooCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🐱 لغة القطط مفعلة في الغرفة: مياووو كياووو 🐾",
                                color = EgyptooCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isMoatazSuperAdmin) {
                                IconButton(
                                    onClick = { viewModel.toggleCatLanguagePrank() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = EgyptooCyan)
                                }
                            }
                        }
                    }
                }

                // 1. Live Voice Broadcast Stage
                AnimatedVisibility(visible = voiceState.isLiveActive) {
                    LiveVoiceStageBar(
                        voiceState = voiceState,
                        isExpanded = uiState.isVoiceStageExpanded,
                        onToggleExpand = { viewModel.toggleVoiceStageExpanded() },
                        onToggleMic = { viewModel.voiceBroadcast.toggleMicMute() },
                        onToggleRaiseHand = { viewModel.voiceBroadcast.toggleRaiseHand() },
                        onSendReaction = { viewModel.voiceBroadcast.sendReaction(it) },
                        onRequestPermission = {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    )
                }

                // 2. Shared Group Music Player
                SharedMusicPlayerBar(
                    musicState = musicState,
                    isExpanded = uiState.isMusicPlayerExpanded,
                    onToggleExpand = { viewModel.toggleMusicPlayerExpanded() },
                    onPlayPause = { viewModel.musicPlayer.togglePlayPause() },
                    onNext = { viewModel.musicPlayer.nextTrack() },
                    onPrev = { viewModel.musicPlayer.prevTrack() },
                    onOpenQueue = { viewModel.setShowMusicQueue(true) },
                    onShareTrack = { viewModel.shareCurrentTrackToChat() }
                )

                // 3. Interactive In-Chat Game Widget
                InChatGameWidget(
                    gameState = gameState,
                    gamesManager = viewModel.gamesManager,
                    onPostResultToChat = { viewModel.postGameScoreToChat(it) }
                )

                // 4. Messages Stream (with Upside-Down flip when prank is active!)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer {
                            if (uiState.isUpsideDownPrankActive) {
                                rotationZ = 180f
                            }
                        },
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        MessageItem(
                            message = msg,
                            showEncryptedMode = uiState.showEncryptedCiphertext,
                            onLaunchGame = { gameType -> viewModel.gamesManager.launchGame(gameType) },
                            onPlaySharedTrack = { _ -> viewModel.musicPlayer.play() },
                            onOpenSecurityVerification = { viewModel.setShowSecurityDialog(true) },
                            onInspectSender = { name, avatar, gender, age ->
                                viewModel.inspectUserFromMessage(name, avatar, gender, age)
                            }
                        )
                    }
                }
            }

            // Floating Emoji Reactions layer
            voiceState.floatingReactions.forEach { reaction ->
                FloatingReactionItem(reaction = reaction)
            }

            // 1. Grand Entrance Royal Celebration Overlay
            GrandEntranceOverlay(
                visible = uiState.isGrandEntranceActive,
                adminName = uiState.userName,
                adminAvatar = uiState.userAvatar,
                onDismiss = { viewModel.dismissGrandEntrance() }
            )

            // 2. Pranks Visual Effects Overlay (Earthquake, Tomato splat, Matrix hacker, Fake 1% battery warning)
            PranksVisualEffectsOverlay(
                isEarthquake = uiState.isEarthquakeActive,
                isTomatoSplat = uiState.isTomatoSplatActive,
                isMatrixHacker = uiState.isMatrixHackerActive,
                fakeBatteryAlert = uiState.fakeBatteryPrankActive,
                onDismissBatteryPrank = { viewModel.dismissFakeBatteryPrank() },
                onCleanTomatoes = { viewModel.toggleTomatoSplatPrank() }
            )
        }
    }

    // Moataz Prank Arsenal Dialog
    if (uiState.showPrankArsenalDialog) {
        PrankArsenalDialog(
            prankTargetAllRooms = uiState.prankTargetAllRooms,
            onTogglePrankTargetScope = { viewModel.togglePrankTargetScope() },
            isUpsideDownActive = uiState.isUpsideDownPrankActive,
            isDiscoActive = uiState.isDiscoPrankActive,
            isCatLanguageActive = uiState.isCatLanguagePrankActive,
            isGhostModeActive = uiState.isGhostModeActive,
            isRoomFrozen = currentRoom?.isFrozenByAdmin == true,
            isEarthquakeActive = uiState.isEarthquakeActive,
            isTomatoSplatActive = uiState.isTomatoSplatActive,
            isMatrixHackerActive = uiState.isMatrixHackerActive,
            isHeliumVoiceActive = uiState.isHeliumVoiceActive,
            isSlowMoActive = uiState.isSlowMoActive,
            isMirrorReverseActive = uiState.isMirrorReverseActive,
            onToggleUpsideDown = { viewModel.toggleUpsideDownPrank() },
            onToggleDisco = { viewModel.toggleDiscoPrank() },
            onToggleCatLanguage = { viewModel.toggleCatLanguagePrank() },
            onToggleGhostMode = { viewModel.toggleGhostMode() },
            onToggleFreezeRoom = {
                currentRoom?.let { r ->
                    viewModel.adminFreezeRoom(r.id, r.isFrozenByAdmin)
                }
            },
            onToggleEarthquake = { viewModel.toggleEarthquakePrank() },
            onToggleTomatoSplat = { viewModel.toggleTomatoSplatPrank() },
            onTriggerFakeBatteryPrank = { viewModel.triggerFakeBatteryPrank() },
            onToggleMatrixHacker = { viewModel.toggleMatrixHackerPrank() },
            onToggleHeliumVoice = { viewModel.toggleHeliumVoicePrank() },
            onToggleSlowMo = { viewModel.toggleSlowMoPrank() },
            onToggleMirrorReverse = { viewModel.toggleMirrorReversePrank() },
            onTriggerFakeKickPrank = { viewModel.triggerFakeKickPrank() },
            onTriggerPotatoMorph = { viewModel.triggerPotatoMorph() },
            onTriggerGiftRain = { viewModel.triggerGiftRain() },
            onTriggerGrandEntrance = { viewModel.triggerGrandEntrance() },
            onPlayMemeSound = { type -> viewModel.playMemeSound(type) },
            onSendGlobalBroadcast = { text -> viewModel.sendGlobalEmergencyBroadcast(text) },
            onSendBroadcastToAllRoomsAndGroups = { text -> viewModel.sendBroadcastToAllRoomsAndGroups(text) },
            onDismiss = { viewModel.setShowPrankArsenalDialog(false) }
        )
    }

    // User Profile Inspection Dialog
    if (uiState.inspectedUserProfile != null) {
        UserProfileDialog(
            user = uiState.inspectedUserProfile!!,
            isCurrentUserAdmin = isMoatazSuperAdmin,
            onDirectChat = { user ->
                viewModel.dismissInspectedUserProfile()
                viewModel.openDirectChatWithUser(user)
            },
            onAddFriend = { user ->
                viewModel.sendFriendRequest(user.name)
            },
            onSendGift = { _ ->
                viewModel.addCoins(50)
            },
            onOpenPranks = {
                viewModel.setShowPrankArsenalDialog(true)
            },
            onEditProfile = {
                viewModel.setShowEditProfileDialog(true)
            },
            onDismiss = { viewModel.dismissInspectedUserProfile() }
        )
    }

    // Edit Profile Dialog
    if (uiState.showEditProfileDialog) {
        EditProfileDialog(
            initialName = uiState.userName,
            initialAvatar = uiState.userAvatar,
            initialGender = uiState.userGender,
            initialAge = uiState.userAge,
            initialBio = uiState.userBio,
            onSaveProfile = { name, avatar, gender, age, bio ->
                viewModel.updateUserProfile(name, avatar, gender, age, bio)
            },
            onDismiss = { viewModel.setShowEditProfileDialog(false) }
        )
    }

    // Security Dialog
    if (uiState.showSecurityDialog) {
        E2EESecurityDialog(
            roomId = uiState.currentRoomId,
            roomName = currentRoom?.name ?: "غرفة الدردشة",
            showEncryptedMode = uiState.showEncryptedCiphertext,
            onToggleEncryptedMode = { viewModel.toggleEncryptedView() },
            onDismiss = { viewModel.setShowSecurityDialog(false) }
        )
    }

    // Games Picker
    if (uiState.showGamesPicker) {
        GamesPickerDialog(
            onSelectGame = { gameType ->
                viewModel.launchGameFromPicker(gameType)
            },
            onDismiss = { viewModel.setShowGamesPicker(false) }
        )
    }

    // Music Queue
    if (uiState.showMusicQueue) {
        MusicQueueDialog(
            playerState = musicState,
            onSelectTrack = { track ->
                viewModel.musicPlayer.selectTrack(track)
                viewModel.setShowMusicQueue(false)
            },
            onAddTrack = { title, artist ->
                viewModel.musicPlayer.addTrackToQueue(title, artist)
            },
            onUpvoteTrack = { trackId ->
                viewModel.musicPlayer.upvoteTrack(trackId)
            },
            onDismiss = { viewModel.setShowMusicQueue(false) }
        )
    }
}

@Composable
private fun FloatingReactionItem(reaction: FloatingReaction) {
    val yOffset = remember { Animatable(300f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(reaction.id) {
        yOffset.animateTo(
            targetValue = -250f,
            animationSpec = tween(durationMillis = 2600)
        )
    }

    LaunchedEffect(reaction.id) {
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 2600)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier.offset {
                IntOffset(
                    x = (reaction.startXRatio * 700).toInt(),
                    y = yOffset.value.toInt()
                )
            }
        ) {
            Text(
                text = reaction.emoji,
                fontSize = (28 * reaction.scale).sp,
                color = Color.White.copy(alpha = alpha.value)
            )
        }
    }
}
