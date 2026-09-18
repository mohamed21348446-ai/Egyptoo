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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserGender
import com.example.ui.components.EditProfileDialog
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooDarkCardHover
import com.example.ui.theme.EgyptooGreen
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary

@Composable
fun ProfileScreen(
    viewModel: ChatViewModel,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSuperAdmin = viewModel.isSuperAdmin()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        containerColor = EgyptooDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EgyptooSecondary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(EgyptooPurple.copy(alpha = 0.2f), Color.Transparent)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(EgyptooSecondary.copy(alpha = 0.2f))
                                    .border(2.5.dp, EgyptooSecondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(uiState.userAvatar.ifEmpty { "👑" }, fontSize = 40.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.userName,
                                    color = EgyptooTextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = uiState.userEmail,
                                color = EgyptooCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Gender & Age Tags (واضح للكل)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (uiState.userGender == UserGender.MALE) Color(0xFF0D47A1).copy(alpha = 0.6f) else Color(0xFF880E4F).copy(alpha = 0.6f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (uiState.userGender == UserGender.MALE) "ولد 👦" else "بنت 👧",
                                        color = if (uiState.userGender == UserGender.MALE) Color(0xFF80D8FF) else Color(0xFFFF80AB),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "العمر: ${uiState.userAge} سنة 🎂",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = uiState.userBio,
                                color = EgyptooTextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Badge(containerColor = EgyptooSecondary, contentColor = Color.Black) {
                                Text(
                                    text = if (isSuperAdmin) "المشرف العام والمالك لـ Egyptoo 👑" else "عضو موثق في مجتمع مصر 🇪🇬",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Edit Profile Button (تغيير الاسم والصورة والجنس والعمر)
                            Button(
                                onClick = { viewModel.setShowEditProfileDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                Text("تعديل بروفايلي وبياناتي ✏️", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Country & Language Settings Item
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setShowCountryLanguageDialog(true) },
                    shape = RoundedCornerShape(14.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = uiState.selectedCountry.flag, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "البلد واللغة المفضلة",
                                    color = EgyptooTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${uiState.selectedCountry.nameAr} (${uiState.selectedCountry.flag}) • ${uiState.selectedLanguage}",
                                    color = EgyptooSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EgyptooTextSecondary)
                    }
                }
            }

            // Quick Access to Admin Supervision (Only for mohamed21348446@gmail.com)
            if (isSuperAdmin) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavigateToAdmin() },
                        shape = RoundedCornerShape(14.dp),
                        color = EgyptooDarkCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(EgyptooSecondary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "لوحة الإشراف العليا (معتز فقط) 👑",
                                        color = EgyptooSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "التحكم بالمجموعات، حظر الحسابات، وصندوق الدعم",
                                        color = EgyptooTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EgyptooSecondary)
                        }
                    }
                }
            }

            // Security & E2EE Info
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EgyptooGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حماية التشفير التام (E2EE) مفعلة",
                                color = EgyptooTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "جميع المحادثات والغرف في تطبيق Egyptoo مشفرة بمفتاح 256-bit CBC ولا يمكن لأي طرف خارجي اعتراضها.",
                            color = EgyptooTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Logout / Switch Account Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.logout() },
                    shape = RoundedCornerShape(14.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "تسجيل الخروج / تبديل الحساب 🚪",
                            color = Color(0xFFFF5252),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

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
}
