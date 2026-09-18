package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EgyptooSecondary

@Composable
fun PranksVisualEffectsOverlay(
    isEarthquake: Boolean,
    isTomatoSplat: Boolean,
    isMatrixHacker: Boolean,
    fakeBatteryAlert: Boolean,
    onDismissBatteryPrank: () -> Unit,
    onCleanTomatoes: () -> Unit
) {
    // 1. Tomato Splat Overlay
    AnimatedVisibility(
        visible = isTomatoSplat,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red.copy(alpha = 0.12f))
                .clickable { onCleanTomatoes() }
                .testTag("tomato_splat_overlay")
        ) {
            // Scattered Splat Emojis
            Text(
                text = "🍅💥",
                fontSize = 54.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 80.dp, start = 30.dp)
            )
            Text(
                text = "🥚🍳",
                fontSize = 48.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 120.dp, end = 40.dp)
            )
            Text(
                text = "🍅💦",
                fontSize = 62.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "🤮🍕",
                fontSize = 44.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 140.dp, start = 40.dp)
            )
            Text(
                text = "🍅💥",
                fontSize = 50.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 30.dp)
            )

            // Bottom banner explaining the prank
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
            ) {
                Text(
                    text = "🍅 مقلب الطماطم والسلايم شغال! اضغط للتنظيف 🧽",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }

    // 2. Matrix Hacker Screen Overlay
    AnimatedVisibility(
        visible = isMatrixHacker,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val matrixTransition = rememberInfiniteTransition(label = "matrix")
        val alphaAnim by matrixTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "alpha"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(20.dp)
                .testTag("matrix_hacker_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "> ROOT_ACCESS_GRANTED::EGYPTOO_MASTERY",
                    color = Color(0xFF00FF66).copy(alpha = alphaAnim),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "> OVERRIDE_ADMIN: mohamed21348446@gmail.com",
                    color = Color(0xFF00FF66).copy(alpha = alphaAnim),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                Text(
                    text = "> 01001101 01001111 01000001 01010100 01000001 01011010",
                    color = Color(0xFF00E676),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Text(
                    text = "> 👑 تم الاستيلاء على التردد الصوتي والمحادثات بنجاح!",
                    color = Color.Yellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }

    // 3. Fake Battery Critical Prank Dialog
    if (fakeBatteryAlert) {
        Dialog(onDismissRequest = onDismissBatteryPrank) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261010)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fake_battery_prank_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "تحذير: طاقة البطارية 1% ⚠️",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "سيتم إيقاف تشغيل الهاتف خلال 10 ثوانٍ لحماية الدوائر الإلكترونية!\n\n(مقلب مضحك من المشرف معتز 👑🤣)",
                        color = Color(0xFFFFCDD2),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onDismissBatteryPrank,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SentimentVerySatisfied, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ههههه وقعت في الفخ! إغلاق المقلب 🤣", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
