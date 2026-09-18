package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FriendUser
import com.example.data.model.UserGender
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooGold
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary

@Composable
fun UserProfileDialog(
    user: FriendUser,
    isCurrentUserAdmin: Boolean,
    onDirectChat: (FriendUser) -> Unit,
    onAddFriend: (FriendUser) -> Unit,
    onSendGift: (FriendUser) -> Unit,
    onOpenPranks: () -> Unit,
    onEditProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    val isUserAdmin = user.isSuperAdmin || user.email.trim().equals("mohamed21348446@gmail.com", ignoreCase = true)
    val isMale = user.gender == UserGender.MALE

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = EgyptooDarkBg,
            border = androidx.compose.foundation.BorderStroke(
                width = if (isUserAdmin) 2.5.dp else 1.5.dp,
                brush = if (isUserAdmin) {
                    Brush.linearGradient(listOf(EgyptooSecondary, EgyptooGold, Color(0xFFFF8F00)))
                } else {
                    Brush.linearGradient(listOf(EgyptooBorder, if (isMale) EgyptooCyan else Color(0xFFFF4081)))
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isUserAdmin) Icons.Default.WorkspacePremium else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isUserAdmin) EgyptooSecondary else EgyptooCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUserAdmin) "الملف الشخصي الملكي ⚜️" else "الملف الشخصي للمستخدم 👤",
                            color = if (isUserAdmin) EgyptooSecondary else EgyptooTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EgyptooTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ornate Royal Banner if Admin
                if (isUserAdmin) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF5D4037), Color(0xFFE65100), Color(0xFFFFB300), Color(0xFF5D4037))
                                )
                            )
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚜️ 👑 مـالـك ومـشـرف عـام تـطـبـيـق EGYPTOO 👑 ⚜️",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Big Avatar with animated ring
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUserAdmin) {
                                Brush.radialGradient(listOf(EgyptooSecondary, Color(0xFFFF6F00)))
                            } else if (isMale) {
                                Brush.radialGradient(listOf(EgyptooCyan, EgyptooPrimary))
                            } else {
                                Brush.radialGradient(listOf(Color(0xFFFF80AB), EgyptooPurple))
                            }
                        )
                        .border(
                            width = 3.dp,
                            color = if (isUserAdmin) EgyptooGold else if (isMale) EgyptooCyan else Color(0xFFFF4081),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.avatar.ifEmpty { if (isUserAdmin) "👑" else if (isMale) "👦" else "👧" },
                        fontSize = 46.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name and Verified Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = user.countryFlag, fontSize = 16.sp)
                }

                Text(
                    text = user.statusMessage.ifEmpty { user.vipBadge },
                    color = EgyptooSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Vital Metadata Cards: GENDER & AGE (واضح للكل بشكل جذاب ومميز)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Gender Card (علامة الجنس: ولد / بنت)
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMale) Color(0xFF0D47A1).copy(alpha = 0.35f) else Color(0xFF880E4F).copy(alpha = 0.35f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isMale) EgyptooCyan else Color(0xFFFF4081)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isMale) Icons.Default.Male else Icons.Default.Female,
                                    contentDescription = null,
                                    tint = if (isMale) EgyptooCyan else Color(0xFFFF4081),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "الجنس",
                                    color = EgyptooTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isMale) "ولد 👦 ♂️" else "بنت 👧 ♀️",
                                color = if (isMale) EgyptooCyan else Color(0xFFFF80AB),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 2. Age Card (العمر واضح للكل)
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EgyptooDarkCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EgyptooSecondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cake,
                                    contentDescription = null,
                                    tint = EgyptooSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "العمر",
                                    color = EgyptooTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${user.age} سنة 🎂",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bio & Details Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgyptooDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "نبذة عني / الحالة:", color = EgyptooTextSecondary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = user.bio.ifEmpty { "عضو فعال في مجتمع وشات Egyptoo ✨" },
                            color = EgyptooTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = EgyptooBorder.copy(alpha = 0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "الرصيد:", color = EgyptooTextSecondary, fontSize = 11.sp)
                            Text(text = "💰 ${user.egyptooCoins} عملة", color = EgyptooGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "البريد الإلكتروني:", color = EgyptooTextSecondary, fontSize = 11.sp)
                            Text(text = user.email.ifEmpty { "مخفي للأمان 🔒" }, color = EgyptooCyan, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Direct Message button
                    Button(
                        onClick = {
                            onDismiss()
                            onDirectChat(user)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_direct_chat_btn")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مراسلة خاصة مشفرة 💬", fontWeight = FontWeight.Bold)
                    }

                    // Pranks button if super admin viewing someone
                    if (isCurrentUserAdmin && !isUserAdmin) {
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenPranks()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EgyptooSecondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("تنفيذ مقلب على هذا العضو 🪄🤣", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Add Friend & Gift Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onAddFriend(user) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة صديق", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onSendGift(user) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Redeem, contentDescription = null, modifier = Modifier.size(14.dp), tint = EgyptooSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إرسال هدية 🎁", fontSize = 11.sp, color = EgyptooSecondary)
                        }
                    }

                    // Edit Profile button if it's the current user
                    if (isUserAdmin && isCurrentUserAdmin) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onEditProfile()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تعديل بيانات ملفي الشخصي ✏️", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
