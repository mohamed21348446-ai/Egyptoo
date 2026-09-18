package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.AdminSupervisionScreen
import com.example.ui.ChatListScreen
import com.example.ui.ChatRoomScreen
import com.example.ui.ChatViewModel
import com.example.ui.CommunityGroupsScreen
import com.example.ui.DirectChatScreen
import com.example.ui.EgyptooNavTab
import com.example.ui.FriendsScreen
import com.example.ui.LoginAuthScreen
import com.example.ui.ProfileScreen
import com.example.ui.SocialFeedScreen
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Mandatory Social Login Gate
                if (!uiState.isAuthenticated) {
                    LoginAuthScreen(
                        onLoginSuccess = { email, name, avatar, provider ->
                            viewModel.login(email, name, avatar, provider)
                        }
                    )
                } else if (uiState.selectedDirectFriend != null) {
                    // 1-on-1 Direct Private Chat
                    BackHandler {
                        viewModel.closeDirectChat()
                    }
                    DirectChatScreen(
                        friend = uiState.selectedDirectFriend!!,
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.closeDirectChat() },
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (uiState.isInsideRoom) {
                    BackHandler {
                        viewModel.navigateBackToChatsList()
                    }
                    ChatRoomScreen(
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.navigateBackToChatsList() },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = EgyptooDarkBg,
                        topBar = {
                            // Royal Global Emergency Alert Banner
                            AnimatedVisibility(
                                visible = uiState.globalBroadcastAlert != null,
                                enter = slideInVertically(),
                                exit = slideOutVertically()
                            ) {
                                uiState.globalBroadcastAlert?.let { alertText ->
                                    Surface(
                                        color = EgyptooSecondary,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "إذاعة ملكية عاجلة: $alertText",
                                                    color = Color.Black,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.dismissGlobalEmergencyBroadcast() },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            EgyptooBottomNavigationBar(
                                currentTab = uiState.selectedNavTab,
                                isSuperAdmin = viewModel.isSuperAdmin(),
                                onSelectTab = { viewModel.selectNavTab(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.selectedNavTab) {
                                EgyptooNavTab.CHATS_AND_ROOMS -> {
                                    ChatListScreen(
                                        viewModel = viewModel,
                                        onOpenRoom = { roomId ->
                                            viewModel.selectRoom(roomId)
                                        },
                                        onNavigateToAdmin = {
                                            viewModel.selectNavTab(EgyptooNavTab.ADMIN_PANEL)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                EgyptooNavTab.SOCIAL_FEED -> {
                                    SocialFeedScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                EgyptooNavTab.FRIENDS_AND_DMS -> {
                                    FriendsScreen(
                                        viewModel = viewModel,
                                        onOpenDirectChat = { friend ->
                                            viewModel.openDirectChat(friend)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                EgyptooNavTab.GROUPS -> {
                                    CommunityGroupsScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                EgyptooNavTab.ADMIN_PANEL -> {
                                    AdminSupervisionScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                EgyptooNavTab.PROFILE -> {
                                    ProfileScreen(
                                        viewModel = viewModel,
                                        onNavigateToAdmin = {
                                            viewModel.selectNavTab(EgyptooNavTab.ADMIN_PANEL)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // Fake Prank Kick Alert Dialog
                if (uiState.fakeKickPrankDialogActive) {
                    Dialog(onDismissRequest = { viewModel.dismissFakeKickPrank() }) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = EgyptooDarkBg,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF5252)),
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
                                    Text(text = "👻🤣", fontSize = 32.sp)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "⚠️ تنبيه إداري عاجل!",
                                    color = Color(0xFFFF5252),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "تم طردك مؤقتاً بواسطة المشرف معتز 👑\nالسبب: خفة دمك المفرطة وضحكتك اللي ملأت الغرفة! 😂",
                                    color = EgyptooTextPrimary,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = { viewModel.dismissFakeKickPrank() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooSecondary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("أنا مسالم.. رجعني للغرفة بسرعة 🤣", color = Color.Black, fontWeight = FontWeight.Bold)
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
private fun EgyptooBottomNavigationBar(
    currentTab: EgyptooNavTab,
    isSuperAdmin: Boolean,
    onSelectTab: (EgyptooNavTab) -> Unit
) {
    NavigationBar(
        containerColor = EgyptooDarkCard,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        // 1. الغرف
        NavigationBarItem(
            selected = currentTab == EgyptooNavTab.CHATS_AND_ROOMS,
            onClick = { onSelectTab(EgyptooNavTab.CHATS_AND_ROOMS) },
            icon = {
                Icon(Icons.Default.Chat, contentDescription = "الغرف")
            },
            label = {
                Text(
                    text = "الغرف",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == EgyptooNavTab.CHATS_AND_ROOMS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = EgyptooPrimary,
                indicatorColor = EgyptooPrimary,
                unselectedIconColor = EgyptooTextSecondary,
                unselectedTextColor = EgyptooTextSecondary
            )
        )

        // 2. المنشورات
        NavigationBarItem(
            selected = currentTab == EgyptooNavTab.SOCIAL_FEED,
            onClick = { onSelectTab(EgyptooNavTab.SOCIAL_FEED) },
            icon = {
                Icon(Icons.Default.DynamicFeed, contentDescription = "المنشورات")
            },
            label = {
                Text(
                    text = "منشورات",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == EgyptooNavTab.SOCIAL_FEED) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = EgyptooPrimary,
                indicatorColor = EgyptooPrimary,
                unselectedIconColor = EgyptooTextSecondary,
                unselectedTextColor = EgyptooTextSecondary
            )
        )

        // 3. الأصدقاء والخاص (Friends & DMs)
        NavigationBarItem(
            selected = currentTab == EgyptooNavTab.FRIENDS_AND_DMS,
            onClick = { onSelectTab(EgyptooNavTab.FRIENDS_AND_DMS) },
            icon = {
                Icon(Icons.Default.Diversity3, contentDescription = "الأصدقاء")
            },
            label = {
                Text(
                    text = "الأصدقاء 💬",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == EgyptooNavTab.FRIENDS_AND_DMS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = EgyptooPrimary,
                indicatorColor = EgyptooPrimary,
                unselectedIconColor = EgyptooTextSecondary,
                unselectedTextColor = EgyptooTextSecondary
            )
        )

        // 4. المجموعات
        NavigationBarItem(
            selected = currentTab == EgyptooNavTab.GROUPS,
            onClick = { onSelectTab(EgyptooNavTab.GROUPS) },
            icon = {
                Icon(Icons.Default.Group, contentDescription = "المجموعات")
            },
            label = {
                Text(
                    text = "مجموعات",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == EgyptooNavTab.GROUPS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = EgyptooPrimary,
                indicatorColor = EgyptooPrimary,
                unselectedIconColor = EgyptooTextSecondary,
                unselectedTextColor = EgyptooTextSecondary
            )
        )

        // 5. الإشراف (حصرياً لحساب معتز)
        if (isSuperAdmin) {
            NavigationBarItem(
                selected = currentTab == EgyptooNavTab.ADMIN_PANEL,
                onClick = { onSelectTab(EgyptooNavTab.ADMIN_PANEL) },
                icon = {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "الإشراف")
                },
                label = {
                    Text(
                        text = "إشراف 👑",
                        fontSize = 10.sp,
                        fontWeight = if (currentTab == EgyptooNavTab.ADMIN_PANEL) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = EgyptooSecondary,
                    indicatorColor = EgyptooSecondary,
                    unselectedIconColor = EgyptooSecondary.copy(alpha = 0.7f),
                    unselectedTextColor = EgyptooSecondary.copy(alpha = 0.7f)
                )
            )
        }

        // 6. الحساب
        NavigationBarItem(
            selected = currentTab == EgyptooNavTab.PROFILE,
            onClick = { onSelectTab(EgyptooNavTab.PROFILE) },
            icon = {
                Icon(Icons.Default.Person, contentDescription = "حسابي")
            },
            label = {
                Text(
                    text = "معتز",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == EgyptooNavTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = EgyptooPrimary,
                indicatorColor = EgyptooPrimary,
                unselectedIconColor = EgyptooTextSecondary,
                unselectedTextColor = EgyptooTextSecondary
            )
        )
    }
}
