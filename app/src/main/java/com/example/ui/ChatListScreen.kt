package com.example.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChatRoomEntity
import com.example.data.model.CountriesProvider
import com.example.ui.components.CountryLanguageDialog
import com.example.ui.components.CreateRoomDialog
import com.example.ui.components.PrivateRoomPinDialog
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooDarkCardHover
import com.example.ui.theme.EgyptooGreen
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTertiary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(
    viewModel: ChatViewModel,
    onOpenRoom: (String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rooms by viewModel.allRooms.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Filter by country and tab
    val filteredRooms = remember(rooms, uiState.selectedCountry, uiState.roomFilterTab, searchQuery) {
        var list = rooms

        // Country filter (if not "ALL")
        if (uiState.selectedCountry.code != "ALL") {
            list = list.filter { it.countryCode == uiState.selectedCountry.code || it.countryCode == "ALL" }
        }

        // Tab filter (0 = الكل, 1 = عامة 🌍, 2 = خاصة 🔒)
        list = when (uiState.roomFilterTab) {
            1 -> list.filter { !it.isPrivate }
            2 -> list.filter { it.isPrivate }
            else -> list
        }

        // Search query
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, true) ||
                it.description.contains(searchQuery, true) ||
                it.lastMessage.contains(searchQuery, true)
            }
        }
        list
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_list_screen"),
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = EgyptooDarkCard,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // English App Logo and Subtitle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(EgyptooPrimary, EgyptooSecondary))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🇪🇬", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Egyptoo",
                                        color = EgyptooSecondary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "✨",
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = "عالم الترفيه والتواصل العربي",
                                    color = EgyptooTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Top Action Icons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Country & Flag button
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.setShowCountryLanguageDialog(true) },
                                color = EgyptooDarkBg,
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = uiState.selectedCountry.flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = uiState.selectedCountry.nameAr, fontSize = 11.sp, color = EgyptooTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Super Admin crown shortcut (if authorized)
                            if (viewModel.isSuperAdmin()) {
                                IconButton(
                                    onClick = onNavigateToAdmin,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(EgyptooSecondary.copy(alpha = 0.2f))
                                ) {
                                    Text("👑", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            IconButton(
                                onClick = { isSearchActive = !isSearchActive },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "بحث", tint = EgyptooTextPrimary)
                            }
                        }
                    }

                    if (isSearchActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث بالاسم، الدولة، أو المحادثات...", fontSize = 12.sp, color = EgyptooTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = EgyptooTextPrimary,
                                unfocusedTextColor = EgyptooTextPrimary,
                                focusedContainerColor = EgyptooDarkBg,
                                unfocusedContainerColor = EgyptooDarkBg,
                                focusedBorderColor = EgyptooPrimary,
                                unfocusedBorderColor = EgyptooBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Flags & Countries Horizontal Carousel
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(CountriesProvider.supportedCountries) { country ->
                            val isSelected = country.code == uiState.selectedCountry.code
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) EgyptooPrimary else EgyptooDarkBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) EgyptooSecondary else EgyptooBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectCountry(country) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(country.flag, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = country.nameAr,
                                        color = if (isSelected) Color.White else EgyptooTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Room Type Filter Tabs (الكل | غرف عامة 🌍 | غرف خاصة 🔒)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EgyptooDarkBg)
                            .padding(3.dp)
                    ) {
                        val tabs = listOf("كل الغرف 🌐", "غرف عامة 🌍", "غرف خاصة 🔒")
                        tabs.forEachIndexed { index, label ->
                            val isSelected = uiState.roomFilterTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(
                                        if (isSelected) {
                                            if (index == 2) EgyptooSecondary else EgyptooPrimary
                                        } else Color.Transparent
                                    )
                                    .clickable { viewModel.setRoomFilterTab(index) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) {
                                        if (index == 2) Color.Black else Color.White
                                    } else EgyptooTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setShowCreateRoomDialog(true) },
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("fab_new_room"),
                containerColor = EgyptooSecondary,
                contentColor = Color.Black
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "إنشاء غرفة")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إنشاء غرفة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        containerColor = EgyptooDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Interactive Hero Hangout Banner
            item {
                HangoutHeroBanner(onOpenMainRoom = {
                    val target = rooms.firstOrNull { it.countryCode == uiState.selectedCountry.code } ?: rooms.firstOrNull()
                    target?.let { onOpenRoom(it.id) }
                })
            }

            // Super Admin Notice Banner if Moataz is logged in
            if (viewModel.isSuperAdmin()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clickable { onNavigateToAdmin() },
                        shape = RoundedCornerShape(12.dp),
                        color = EgyptooSecondary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "مرحباً بك يا معتز (المشرف العام لـ Egyptoo)",
                                        color = EgyptooSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "الحساب المرخص: ${uiState.userEmail} • انقر لفتح لوحة التحكم",
                                        color = EgyptooTextPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Badge(containerColor = EgyptooSecondary, contentColor = Color.Black) {
                                Text("لوحة الإشراف", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Room items list
            if (filteredRooms.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("لا توجد غرف تطابق هذا الفلتر", color = EgyptooTextPrimary, fontSize = 14.sp)
                            Text("يمكنك إنشاء غرفة جديدة بسهولة بالضغط على الزر بالأسفل", color = EgyptooTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                items(filteredRooms, key = { it.id }) { room ->
                    ChatRoomListItem(
                        room = room,
                        onClick = { viewModel.selectRoom(room.id) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showCountryLanguageDialog) {
        CountryLanguageDialog(
            selectedCountry = uiState.selectedCountry,
            selectedLanguage = uiState.selectedLanguage,
            onSelectCountry = { viewModel.selectCountry(it) },
            onSelectLanguage = { viewModel.selectLanguage(it) },
            onDismiss = { viewModel.setShowCountryLanguageDialog(false) }
        )
    }

    if (uiState.showCreateRoomDialog) {
        CreateRoomDialog(
            onDismiss = { viewModel.setShowCreateRoomDialog(false) },
            onCreateRoom = { name, desc, isPrivate, pin, code, flag, cat ->
                viewModel.createNewRoom(name, desc, isPrivate, pin, code, flag, cat)
            }
        )
    }

    if (uiState.privateRoomPinPromptId != null) {
        PrivateRoomPinDialog(
            errorMessage = uiState.privateRoomError,
            onConfirmPin = { pin -> viewModel.verifyAndEnterPrivateRoom(pin) },
            onDismiss = { viewModel.dismissPrivatePinPrompt() }
        )
    }
}

@Composable
private fun HangoutHeroBanner(onOpenMainRoom: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenMainRoom() },
        color = EgyptooDarkCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooPrimary.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chat_group_banner_1789764535300),
                    contentDescription = "غرفة التحديات والموسيقى في Egyptoo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, EgyptooDarkCard)
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ملتقى الغرف الصوتية والتحديات 🎧🎮",
                        color = EgyptooTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "بث صوتي مباشر • ألعاب جماعية تفاعلية • مشغل موسيقى مشترك",
                        color = EgyptooCyan,
                        fontSize = 11.sp
                    )
                }
                Badge(containerColor = EgyptooPrimary, contentColor = Color.White) {
                    Text("انضم الآن 🚀", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatRoomListItem(
    room: ChatRoomEntity,
    onClick: () -> Unit
) {
    val timeFormatted = remember(room.lastMessageTimestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(room.lastMessageTimestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("room_item_${room.id}"),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country flag / Avatar with live voice pulse
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EgyptooDarkCardHover)
                        .border(
                            1.5.dp,
                            if (room.isLiveVoiceActive) EgyptooGreen else EgyptooBorder,
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = room.avatarInitials, fontSize = 26.sp)
                }

                if (room.isLiveVoiceActive) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(EgyptooGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Radio, contentDescription = null, tint = Color.Black, modifier = Modifier.size(11.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Room info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = room.name,
                            color = EgyptooTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (room.isPrivate) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Lock, contentDescription = "غرفة خاصة", tint = EgyptooSecondary, modifier = Modifier.size(12.dp))
                        }
                    }

                    Text(
                        text = timeFormatted,
                        color = if (room.unreadCount > 0) EgyptooSecondary else EgyptooTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (room.isLiveVoiceActive) {
                            Text(text = "🎙️ بث مباشر • ", color = EgyptooGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else if (room.activeMusicTrackTitle.isNotEmpty()) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = EgyptooCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                        }

                        Text(
                            text = room.lastMessage,
                            color = EgyptooTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (room.isPrivate) {
                            Badge(containerColor = EgyptooSecondary.copy(alpha = 0.2f), contentColor = EgyptooSecondary) {
                                Text("خاصة", fontSize = 9.sp, modifier = Modifier.padding(horizontal = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        if (room.unreadCount > 0) {
                            Badge(
                                containerColor = EgyptooPrimary,
                                contentColor = Color.White
                            ) {
                                Text("${room.unreadCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
