package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthProvider
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooGreen
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary

@Composable
fun LoginAuthScreen(
    onLoginSuccess: (email: String, name: String, avatar: String, provider: AuthProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomInputDialog by remember { mutableStateOf(false) }
    var customProvider by remember { mutableStateOf(AuthProvider.GOOGLE) }
    var customEmail by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EgyptooDarkBg)
            .testTag("login_auth_screen")
    ) {
        // Glowing background accents
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(EgyptooPrimary.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo and Icon
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(EgyptooPrimary, EgyptooSecondary, EgyptooCyan)
                        )
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(EgyptooDarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🇪🇬",
                        fontSize = 42.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Name & Subtitle
            Text(
                text = "Egyptoo",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Text(
                text = "عالم الترفيه، التواصل العربي والغرف التفاعلية",
                color = EgyptooTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Compulsory Notice Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(16.dp),
                color = EgyptooDarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EgyptooSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = EgyptooSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "تسجيل الدخول الإجباري 🔐",
                            color = EgyptooSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "يجب ربط التطبيق بحساب Google أو Facebook للتحقق من هوية العضوية وتأمين الغرف المشفرة.",
                            color = EgyptooTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 1. One-Tap Super Admin Moataz Google Login
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onLoginSuccess(
                            "mohamed21348446@gmail.com",
                            "معتز",
                            "👑",
                            AuthProvider.GOOGLE
                        )
                    }
                    .testTag("login_as_moataz_btn"),
                shape = RoundedCornerShape(18.dp),
                color = EgyptooDarkCard,
                border = androidx.compose.foundation.BorderStroke(2.dp, EgyptooSecondary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(EgyptooSecondary, Color(0xFFFF8F00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "دخول فوري كـ معتز (المشرف العام 👑)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "mohamed21348446@gmail.com (Google)",
                            color = EgyptooSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EgyptooSecondary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "المالك ⚡",
                            color = EgyptooSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Sign in with Google Button
            Button(
                onClick = {
                    customProvider = AuthProvider.GOOGLE
                    showCustomInputDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_login_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "متابعة باستخدام حساب Google",
                        color = Color(0xFF202124),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Sign in with Facebook Button
            Button(
                onClick = {
                    customProvider = AuthProvider.FACEBOOK
                    showCustomInputDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("facebook_login_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "f",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "متابعة باستخدام حساب Facebook",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Account Testing Switcher (To see standard user mode vs Admin mode)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = EgyptooBorder)
                Text(
                    text = "  أو تجربة كعضو عادي  ",
                    color = EgyptooTextSecondary,
                    fontSize = 11.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = EgyptooBorder)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Demo Guest 1
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onLoginSuccess(
                                "omar_alfahad@gmail.com",
                                "عمر الفهد",
                                "⚡",
                                AuthProvider.GOOGLE
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "عمر الفهد",
                                color = EgyptooTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "عضو عادي (جوجل)",
                                color = EgyptooTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Demo Guest 2
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onLoginSuccess(
                                "sara_mansoor@facebook.com",
                                "سارة المنصور",
                                "🎨",
                                AuthProvider.FACEBOOK
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "سارة المنصور",
                                color = EgyptooTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "عضو عادي (فيسبوك)",
                                color = EgyptooTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Custom Account Input Dialog / Box
            AnimatedVisibility(visible = showCustomInputDialog) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (customProvider == AuthProvider.GOOGLE) "تسجيل الدخول عبر حساب Google 🌐" else "تسجيل الدخول عبر حساب Facebook 📘",
                            color = EgyptooTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customEmail,
                            onValueChange = { customEmail = it },
                            label = { Text("البريد الإلكتروني", fontSize = 11.sp) },
                            placeholder = { Text("your_name@gmail.com", fontSize = 11.sp) },
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

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("الاسم المستعار في Egyptoo", fontSize = 11.sp) },
                            placeholder = { Text("معتز، أحمد، كريم...", fontSize = 11.sp) },
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showCustomInputDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("إلغاء", color = EgyptooTextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val email = customEmail.trim().ifEmpty { "user_${System.currentTimeMillis().toString().takeLast(4)}@gmail.com" }
                                    val name = customName.trim().ifEmpty { "عضو Egyptoo" }
                                    val avatar = if (email.equals("mohamed21348446@gmail.com", ignoreCase = true)) "👑" else "🌟"
                                    showCustomInputDialog = false
                                    onLoginSuccess(email, name, avatar, customProvider)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (customProvider == AuthProvider.GOOGLE) EgyptooPrimary else Color(0xFF1877F2)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تأكيد الدخول", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer info: E2EE and policies
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = EgyptooGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "جميع الجلسات والبيانات مشفرة بتشفير 256-bit E2EE",
                    color = EgyptooTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
