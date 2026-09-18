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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.audio.MemeSoundType
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
import com.example.data.model.BannedAccount
import com.example.data.model.ChatRoomEntity
import com.example.data.model.SupportTicket
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

@Composable
fun AdminSupervisionScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val rooms by viewModel.allRooms.collectAsState()
    val tickets by viewModel.supportTickets.collectAsState()
    val bannedAccounts by viewModel.bannedAccounts.collectAsState()

    val isAuthorized = viewModel.isSuperAdmin()
    var selectedTab by remember { mutableStateOf(0) } // 0 = إشراف المجموعات, 1 = حظر الحسابات, 2 = الدعم والشكاوى

    // New Ban input states
    var banTargetEmail by remember { mutableStateOf("") }
    var banTargetName by remember { mutableStateOf("") }
    var banReason by remember { mutableStateOf("") }

    // Support ticket reply state
    var selectedTicketForReply by remember { mutableStateOf<SupportTicket?>(null) }
    var replyMessageText by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_supervision_screen"),
        containerColor = EgyptooDarkBg
    ) { innerPadding ->
        if (!isAuthorized) {
            // Unauthorized warning screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "منطقة إشراف محظورة ⛔",
                            color = Color(0xFFFF5252),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "عذراً! لوحة الإشراف على المجموعات وحظر الحسابات وقسم الدعم مخصصة حصرياً للمشرف العام معتز عبر البريد المرخص:\nmohamed21348446@gmail.com",
                            color = EgyptooTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            return@Scaffold
        }

        // Authorized Super Admin Moataz View
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Super Admin Profile & Verification Banner
            item {
                SuperAdminHeroBanner(email = uiState.userEmail, name = uiState.userName)
            }

            // Admin feedback snack/card if any
            if (uiState.adminFeedbackMessage != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = EgyptooGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EgyptooGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.adminFeedbackMessage ?: "",
                                    color = EgyptooGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = { viewModel.clearAdminFeedbackMessage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EgyptooGreen)
                            }
                        }
                    }
                }
            }

            // 2. Control Tabs (المجموعات | حظر الحسابات | الدعم | المقالب والصلاحيات)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EgyptooDarkCard)
                        .padding(4.dp)
                ) {
                    AdminTabButton(
                        title = "المجموعات 👥",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    AdminTabButton(
                        title = "حظر الحسابات ⛔",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    AdminTabButton(
                        title = "الدعم 💬",
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                    AdminTabButton(
                        title = "المقالب 🪄",
                        isSelected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Tab Contents
            when (selectedTab) {
                0 -> {
                    // Group Supervision Tab
                    item {
                        Text(
                            text = "إشراف المجموعات والغرف (التحكم والتجميد)",
                            color = EgyptooTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(rooms, key = { it.id }) { room ->
                        AdminRoomCard(
                            room = room,
                            onToggleFreeze = { viewModel.adminFreezeRoom(room.id, room.isFrozenByAdmin) },
                            onDelete = { viewModel.adminDeleteRoom(room.id) }
                        )
                    }
                }

                1 -> {
                    // Account Moderation / Ban Tab
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = EgyptooDarkCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PersonOff, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "حظر حساب أو عضو مخالف فورياً",
                                        color = EgyptooTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = banTargetEmail,
                                    onValueChange = { banTargetEmail = it },
                                    label = { Text("البريد الإلكتروني للحساب", fontSize = 11.sp) },
                                    placeholder = { Text("user@example.com", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = EgyptooTextPrimary,
                                        unfocusedTextColor = EgyptooTextPrimary,
                                        focusedContainerColor = EgyptooDarkBg,
                                        unfocusedContainerColor = EgyptooDarkBg,
                                        focusedBorderColor = Color(0xFFFF5252),
                                        unfocusedBorderColor = EgyptooBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = banTargetName,
                                    onValueChange = { banTargetName = it },
                                    label = { Text("اسم المستخدم (اختياري)", fontSize = 11.sp) },
                                    placeholder = { Text("مثال: حساب مخالف", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = EgyptooTextPrimary,
                                        unfocusedTextColor = EgyptooTextPrimary,
                                        focusedContainerColor = EgyptooDarkBg,
                                        unfocusedContainerColor = EgyptooDarkBg,
                                        focusedBorderColor = Color(0xFFFF5252),
                                        unfocusedBorderColor = EgyptooBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = banReason,
                                    onValueChange = { banReason = it },
                                    label = { Text("سبب الحظر", fontSize = 11.sp) },
                                    placeholder = { Text("إساءة، رسائل مزعجة، مخالفة الشروط", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = EgyptooTextPrimary,
                                        unfocusedTextColor = EgyptooTextPrimary,
                                        focusedContainerColor = EgyptooDarkBg,
                                        unfocusedContainerColor = EgyptooDarkBg,
                                        focusedBorderColor = Color(0xFFFF5252),
                                        unfocusedBorderColor = EgyptooBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.adminBanAccount(banTargetEmail, banTargetName, banReason)
                                        banTargetEmail = ""
                                        banTargetName = ""
                                        banReason = ""
                                    },
                                    enabled = banTargetEmail.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تطبيق الحظر الفوري", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "قائمة الحسابات المحظورة حالياً (${bannedAccounts.size}):",
                            color = EgyptooTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (bannedAccounts.isEmpty()) {
                        item {
                            Text("لا توجد حسابات محظورة حالياً. جميع الأعضاء ملتزمون بالقوانين.", color = EgyptooTextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        items(bannedAccounts, key = { it.email }) { banned ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = EgyptooDarkCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = banned.email, color = Color(0xFFFF5252), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "الاسم: ${banned.userName} • السبب: ${banned.reason}", color = EgyptooTextSecondary, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.adminUnbanAccount(banned.email) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EgyptooGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("فك الحظر", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Support & User Complaints Desk
                    item {
                        Text(
                            text = "صندوق الدعم الفني والشكاوى من الأعضاء:",
                            color = EgyptooTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(tickets, key = { it.id }) { ticket ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = EgyptooDarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (ticket.status == "RESOLVED") EgyptooGreen.copy(alpha = 0.4f) else EgyptooSecondary.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ticket.subject,
                                        color = EgyptooTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Badge(
                                        containerColor = if (ticket.status == "RESOLVED") EgyptooGreen else EgyptooSecondary,
                                        contentColor = Color.Black
                                    ) {
                                        Text(
                                            if (ticket.status == "RESOLVED") "تم الحل ✅" else "قيد الانتظار ⏳",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "المرسل: ${ticket.userName} (${ticket.userEmail})",
                                    color = EgyptooCyan,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ticket.message,
                                    color = EgyptooTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                if (ticket.adminReply != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EgyptooDarkBg,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = "رد المشرف معتز 👑:",
                                                color = EgyptooSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = ticket.adminReply,
                                                color = EgyptooTextPrimary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            selectedTicketForReply = ticket
                                            replyMessageText = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("الرد وحل الشكوى كـ معتز 👑", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Tab 3: المقالب والصلاحيات الخارقة لمعتز
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = EgyptooDarkCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪄👑", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "ترسانة مقالب وصلاحيات المشرف معتز",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "مفعلة حصرياً لحسابك mohamed21348446@gmail.com",
                                            color = EgyptooSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.setShowPrankArsenalDialog(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooSecondary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("فتح لوحة تحكم المقالب التفاعلية الكاملة 🎭", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "إجراءات مقالب سريعة وفورية:",
                                    color = EgyptooTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.toggleUpsideDownPrank() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isUpsideDownPrankActive) Color(0xFFFF9100) else EgyptooDarkBg
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("قلب الشاشة 🙃", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.toggleCatLanguagePrank() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isCatLanguagePrankActive) EgyptooCyan else EgyptooDarkBg
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("لغة القطط 🐱", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.toggleDiscoPrank() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isDiscoPrankActive) EgyptooPurple else EgyptooDarkBg
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("ديسكو الغرفة 🪩", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.triggerPotatoMorph() },
                                        colors = ButtonDefaults.buttonColors(containerColor = EgyptooDarkBg),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("تحويل لبطاطس 🥔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.triggerFakeKickPrank() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.8f)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("طرد وهمي 👻", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.triggerGiftRain() },
                                        colors = ButtonDefaults.buttonColors(containerColor = EgyptooGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("مطر الجواهر 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reply Dialog for Support Ticket
    if (selectedTicketForReply != null) {
        val ticket = selectedTicketForReply!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectedTicketForReply = null },
            containerColor = EgyptooDarkCard,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "الرد على تذكرة: ${ticket.subject}",
                    color = EgyptooTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("المستخدم: ${ticket.userName}", color = EgyptooCyan, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = replyMessageText,
                        onValueChange = { replyMessageText = it },
                        label = { Text("اكتب رد المشرف معتز 👑", fontSize = 12.sp) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminReplySupportTicket(ticket.id, replyMessageText)
                        selectedTicketForReply = null
                    },
                    enabled = replyMessageText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إرسال الرد وحل التذكرة", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { selectedTicketForReply = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("إلغاء", color = EgyptooTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SuperAdminHeroBanner(email: String, name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = EgyptooDarkCard,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, EgyptooSecondary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(EgyptooPurple.copy(alpha = 0.25f), EgyptooSecondary.copy(alpha = 0.15f))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(EgyptooSecondary.copy(alpha = 0.25f))
                            .border(2.dp, EgyptooSecondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المشرف العام: $name",
                                color = EgyptooSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = email,
                            color = EgyptooCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "لوحة الإشراف الحصرية • تحكم كامل بالمجموعات والحظر والدعم",
                            color = EgyptooTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) EgyptooPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else EgyptooTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun AdminRoomCard(
    room: ChatRoomEntity,
    onToggleFreeze: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = EgyptooDarkCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (room.isFrozenByAdmin) Color(0xFFFF5252).copy(alpha = 0.5f) else EgyptooBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = room.countryFlag, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.name,
                                color = EgyptooTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (room.isPrivate) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(13.dp))
                            }
                        }
                        Text(
                            text = "${room.category} • ${room.memberCount} عضو • ${if (room.isPrivate) "خاصة (PIN: ${room.accessPin})" else "عامة"}",
                            color = EgyptooTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                if (room.isFrozenByAdmin) {
                    Badge(containerColor = Color(0xFFFF5252), contentColor = Color.White) {
                        Text("مجمدة ❄️", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggleFreeze,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (room.isFrozenByAdmin) EgyptooGreen else Color(0xFFF59E0B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text(
                        text = if (room.isFrozenByAdmin) "فك التجميد ▶️" else "تجميد الغرفة ❄️",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFFF5252), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حذف", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
