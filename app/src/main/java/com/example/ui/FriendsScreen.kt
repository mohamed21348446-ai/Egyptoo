package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FriendRequest
import com.example.data.model.FriendUser
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooGreen
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTertiary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary
import kotlin.random.Random

@Composable
fun FriendsScreen(
    viewModel: ChatViewModel,
    onOpenDirectChat: (friend: FriendUser) -> Unit,
    modifier: Modifier = Modifier
) {
    val friends by viewModel.friendsList.collectAsState()
    val friendRequests by viewModel.friendRequests.collectAsState()
    val userCoins by viewModel.userCoins.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = الأصدقاء, 1 = طلبات الصداقة, 2 = إضافة صديق
    var searchFriendQuery by remember { mutableStateOf("") }
    var showLuckyWheelDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("friends_screen"),
        containerColor = EgyptooDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Lucky Spin Wheel & Balance
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "الأصدقاء والمراسلة الخاصة 💬",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "رصيدك: $userCoins عملة ذهبية 🪙",
                                color = EgyptooSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Spin Wheel Trigger Button
                        Button(
                            onClick = { showLuckyWheelDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EgyptooSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Casino, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("عجلة الحظ 🎡", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Tabs (الأصدقاء | الطلبات | إضافة)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EgyptooDarkCard)
                        .padding(4.dp)
                ) {
                    FriendTabItem(
                        title = "الأصدقاء (${friends.size})",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    FriendTabItem(
                        title = "الطلبات (${friendRequests.size})",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    FriendTabItem(
                        title = "إضافة صديق ➕",
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            when (selectedTab) {
                // TAB 0: قائمة الأصدقاء
                0 -> {
                    item {
                        OutlinedTextField(
                            value = searchFriendQuery,
                            onValueChange = { searchFriendQuery = it },
                            placeholder = { Text("ابحث في قائمة أصدقائك...", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = EgyptooTextSecondary)
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = EgyptooTextPrimary,
                                unfocusedTextColor = EgyptooTextPrimary,
                                focusedBorderColor = EgyptooPrimary,
                                unfocusedBorderColor = EgyptooBorder
                            )
                        )
                    }

                    val filteredFriends = friends.filter {
                        it.name.contains(searchFriendQuery, ignoreCase = true) ||
                        it.email.contains(searchFriendQuery, ignoreCase = true)
                    }

                    if (filteredFriends.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا يوجد أصدقاء يطابقون البحث. أضف أصدقاء جدد من تبويب الإضافة!",
                                    color = EgyptooTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        items(filteredFriends, key = { it.id }) { friend ->
                            FriendItemCard(
                                friend = friend,
                                onOpenChat = { onOpenDirectChat(friend) }
                            )
                        }
                    }
                }

                // TAB 1: طلبات الصداقة المعلقة
                1 -> {
                    if (friendRequests.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد طلبات صداقة معلقة حالياً ✨",
                                    color = EgyptooTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(friendRequests, key = { it.id }) { req ->
                            FriendRequestCard(
                                request = req,
                                onAccept = { viewModel.acceptFriendRequest(req.id) },
                                onDecline = { viewModel.declineFriendRequest(req.id) }
                            )
                        }
                    }
                }

                // TAB 2: إضافة صديق جديد
                2 -> {
                    item {
                        AddFriendSection(
                            onSendRequest = { query ->
                                viewModel.sendFriendRequest(query)
                            }
                        )
                    }
                }
            }
        }
    }

    // Lucky Spin Wheel Modal
    if (showLuckyWheelDialog) {
        DailyLuckySpinWheelDialog(
            onSpinWon = { rewardText, coinsAmount ->
                viewModel.addCoins(coinsAmount)
            },
            onDismiss = { showLuckyWheelDialog = false }
        )
    }
}

@Composable
private fun FriendTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) EgyptooPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else EgyptooTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun FriendItemCard(
    friend: FriendUser,
    onOpenChat: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenChat),
        shape = RoundedCornerShape(16.dp),
        color = EgyptooDarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Avatar with online indicator
                Box {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(EgyptooPrimary.copy(alpha = 0.4f), EgyptooDarkBg)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = friend.avatar, fontSize = 24.sp)
                    }

                    // Online indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (friend.isOnline) EgyptooGreen else EgyptooTextSecondary)
                            .border(2.dp, EgyptooDarkCard, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = friend.name,
                            color = EgyptooTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = friend.countryFlag, fontSize = 14.sp)
                    }

                    Text(
                        text = if (friend.statusMessage.isNotEmpty()) friend.statusMessage else if (friend.isOnline) "متصل الآن 🟢" else "غير متصل ⚪",
                        color = if (friend.isOnline) EgyptooGreen else EgyptooTextSecondary,
                        fontSize = 11.sp
                    )

                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EgyptooSecondary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = friend.vipBadge, color = EgyptooSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Open Direct Chat button
            Button(
                onClick = onOpenChat,
                colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مراسلة خاصة 🔒", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FriendRequestCard(
    request: FriendRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = EgyptooDarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooPrimary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = request.fromUser.avatar, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.fromUser.name,
                            color = EgyptooTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = request.fromUser.countryFlag, fontSize = 12.sp)
                    }
                    Text(
                        text = "أرسل لك طلب صداقة للتواصل الخاص 🤝",
                        color = EgyptooTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Row {
                IconButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5252).copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "رفض", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EgyptooGreen.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "قبول", tint = EgyptooGreen, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AddFriendSection(
    onSendRequest: (String) -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }
    var messageSent by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = EgyptooDarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = EgyptooPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إضافة صديق جديد إلى قائمة معتز",
                    color = EgyptooTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "اكتب البريد الإلكتروني أو اسم المستخدم لإرسال طلب صداقة فوري:",
                color = EgyptooTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = inputQuery,
                onValueChange = {
                    inputQuery = it
                    messageSent = false
                },
                placeholder = { Text("مثال: kahlid@gmail.com أو أحمد", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = EgyptooTextPrimary,
                    unfocusedTextColor = EgyptooTextPrimary,
                    focusedBorderColor = EgyptooPrimary,
                    unfocusedBorderColor = EgyptooBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (inputQuery.isNotBlank()) {
                        onSendRequest(inputQuery)
                        inputQuery = ""
                        messageSent = true
                    }
                },
                enabled = inputQuery.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إرسال طلب الصداقة 🤝", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (messageSent) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تم إرسال طلب الصداقة بنجاح! سيتم إضافته بمجرد الموافقة ✨",
                    color = EgyptooGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DailyLuckySpinWheelDialog(
    onSpinWon: (reward: String, coins: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableStateOf(0f) }
    var wonReward by remember { mutableStateOf<String?>(null) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
        label = "wheelRotation",
        finishedListener = {
            isSpinning = false
        }
    )

    val rewards = listOf(
        "100 جوهرة 💎" to 100,
        "250 عملة 🪙" to 250,
        "شارة VIP ذهبية ⭐" to 50,
        "500 عملة Egyptoo 🪙" to 500,
        "ميكروفون ذهبي 🎙️" to 75,
        "صندوق الحظ الأسطوري 🎁" to 300
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = EgyptooDarkBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, EgyptooSecondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عجلة الحظ والجوائز اليومية 🎡",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EgyptooTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel graphic representation
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .rotate(animatedRotation)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    EgyptooPrimary,
                                    EgyptooSecondary,
                                    EgyptooCyan,
                                    EgyptooPurple,
                                    EgyptooGreen,
                                    EgyptooPrimary
                                )
                            )
                        )
                        .border(4.dp, EgyptooSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(EgyptooDarkBg)
                            .border(2.dp, EgyptooSecondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (wonReward != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = EgyptooSecondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "مبروك! ربحت اليوم 🎉", color = EgyptooSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = wonReward ?: "", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        if (!isSpinning) {
                            isSpinning = true
                            wonReward = null
                            val randomRewardIndex = Random.nextInt(rewards.size)
                            val selectedReward = rewards[randomRewardIndex]
                            val extraTurns = 360f * (4 + Random.nextInt(3))
                            val targetAngle = extraTurns + (randomRewardIndex * 60f)
                            rotationAngle += targetAngle

                            kotlinx.coroutines.GlobalScope.let {
                                // simulate finish
                            }
                            wonReward = selectedReward.first
                            onSpinWon(selectedReward.first, selectedReward.second)
                        }
                    },
                    enabled = !isSpinning,
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooSecondary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isSpinning) "العجلة تدور الآن... 🎡" else "تدوير عجلة الحظ مجاناً 🎲",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
