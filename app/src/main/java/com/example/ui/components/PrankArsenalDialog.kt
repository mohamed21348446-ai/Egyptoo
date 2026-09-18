package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.audio.MemeSoundType
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooGold
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary

@Composable
fun PrankArsenalDialog(
    prankTargetAllRooms: Boolean,
    onTogglePrankTargetScope: () -> Unit,
    isUpsideDownActive: Boolean,
    isDiscoActive: Boolean,
    isCatLanguageActive: Boolean,
    isGhostModeActive: Boolean,
    isRoomFrozen: Boolean,
    isEarthquakeActive: Boolean,
    isTomatoSplatActive: Boolean,
    isMatrixHackerActive: Boolean,
    isHeliumVoiceActive: Boolean,
    isSlowMoActive: Boolean,
    isMirrorReverseActive: Boolean,
    onToggleUpsideDown: () -> Unit,
    onToggleDisco: () -> Unit,
    onToggleCatLanguage: () -> Unit,
    onToggleGhostMode: () -> Unit,
    onToggleFreezeRoom: () -> Unit,
    onToggleEarthquake: () -> Unit,
    onToggleTomatoSplat: () -> Unit,
    onTriggerFakeBatteryPrank: () -> Unit,
    onToggleMatrixHacker: () -> Unit,
    onToggleHeliumVoice: () -> Unit,
    onToggleSlowMo: () -> Unit,
    onToggleMirrorReverse: () -> Unit,
    onTriggerFakeKickPrank: () -> Unit,
    onTriggerPotatoMorph: () -> Unit,
    onTriggerGiftRain: () -> Unit,
    onTriggerGrandEntrance: () -> Unit,
    onPlayMemeSound: (MemeSoundType) -> Unit,
    onSendGlobalBroadcast: (String) -> Unit,
    onSendBroadcastToAllRoomsAndGroups: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var broadcastText by remember { mutableStateOf("") }
    var allRoomsBroadcastText by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf(0) } // 0 = مقالب هزلية, 1 = ساوندبورد ميمز, 2 = صلاحيات وبث ملكي

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EgyptooDarkBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, EgyptooSecondary),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prank_arsenal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Moataz badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(EgyptooSecondary, Color(0xFFFF8F00))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ترسانة مقالب وصلاحيات معتز 🪄",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "حصرياً للمشرف والمالك العام ⚡",
                                color = EgyptooSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EgyptooTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PRANK TARGET SCOPE (الغرفة الحالية vs جميع المجموعات والغرف)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTogglePrankTargetScope() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (prankTargetAllRooms) Color(0xFFE65100).copy(alpha = 0.25f) else EgyptooDarkCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (prankTargetAllRooms) EgyptooSecondary else EgyptooBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (prankTargetAllRooms) "🌐" else "📍", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (prankTargetAllRooms) "النطاق: جميع الغرف والمجموعات 🌐" else "النطاق: الغرفة الحالية فقط 📍",
                                    color = if (prankTargetAllRooms) EgyptooSecondary else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (prankTargetAllRooms) "المقالب والصلاحيات تؤثر على كل الغرف دفعة واحدة" else "المقالب تنفذ فقط داخل غرفتك الحالية",
                                    color = EgyptooTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = prankTargetAllRooms,
                            onCheckedChange = { onTogglePrankTargetScope() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EgyptooSecondary,
                                checkedTrackColor = EgyptooSecondary.copy(alpha = 0.4f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EgyptooDarkCard)
                        .padding(3.dp)
                ) {
                    PrankTabItem(
                        title = "خدع ومقالب 🎭",
                        isSelected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    PrankTabItem(
                        title = "أصوات ميمز 🦆",
                        isSelected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    PrankTabItem(
                        title = "صلاحيات ملكية 👑",
                        isSelected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedSection) {
                    // SECTION 0: مقالب هزلية مضحكة موسعة
                    0 -> {
                        // 1. مقلب الزلزال والاهتزاز العنيف
                        PrankToggleCard(
                            emoji = "🌋",
                            title = "زلزال واهتزاز الشاشة العنيف",
                            desc = "يهز شاشة الغرفة بعنف مع صوت زئير زلزالي مضحك 📳!",
                            isActive = isEarthquakeActive,
                            activeLabel = "الزلزال شغال ويهز الشاشة! 🌋",
                            onClick = onToggleEarthquake
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. مقلب عاصفة الطماطم والبيض
                        PrankToggleCard(
                            emoji = "🍅",
                            title = "عاصفة الطماطم والسلايم على الشاشة",
                            desc = "يلطخ شاشات الأعضاء بطماطم وبيض ومسحات سلايم مضحكة!",
                            isActive = isTomatoSplatActive,
                            activeLabel = "الشاشة ملطخة بالطماطم 🍅🍳",
                            onClick = onToggleTomatoSplat
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. مقلب إنذار البطارية 1%
                        PrankActionCard(
                            emoji = "🔋",
                            title = "مقلب بطارية الهاتف 1% (Fake Battery)",
                            desc = "يظهر شاشة تحذير حمراء مرعبة للأعضاء بأن هاتفهم سينطفئ خلال ثوانٍ!",
                            actionText = "إطلاق فخ البطارية 1% ⚠️",
                            actionColor = Color(0xFFFF5252),
                            onClick = onTriggerFakeBatteryPrank
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4. مقلب شاشة اختراق مصفوفة الهكر
                        PrankToggleCard(
                            emoji = "👨‍💻",
                            title = "اختراق مصفوفة الهكر الخضراء (Matrix)",
                            desc = "يغطي الشاشة بشفرة هكر خضراء مرعبة باسم المشرف معتز!",
                            isActive = isMatrixHackerActive,
                            activeLabel = "مصفوفة الهكر نشطة 💚",
                            onClick = onToggleMatrixHacker
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5. صوت الهيليوم والفضائي
                        PrankToggleCard(
                            emoji = "🎈",
                            title = "فلتر صوت الهيليوم والفضائيين",
                            desc = "يحول رسائلك وكلامك إلى صوت هيليوم فضائي ساخر 👽!",
                            isActive = isHeliumVoiceActive,
                            activeLabel = "صوت الهيليوم مفعل 🎈",
                            onClick = onToggleHeliumVoice
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 6. مقلب السلحفاة والحركة البطيئة
                        PrankToggleCard(
                            emoji = "🐢",
                            title = "وضع السلحفاة والحركة البطيئة (Slow-Mo)",
                            desc = "يبطئ ظهور الكلمات مع صوت صراصير الليل الهادئة!",
                            isActive = isSlowMoActive,
                            activeLabel = "وضع السلحفاة شغال 🐢",
                            onClick = onToggleSlowMo
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 7. مرآة الكلام المعكوسة
                        PrankToggleCard(
                            emoji = "🪞",
                            title = "مرآة الكلام المعكوس (Mirror Mode)",
                            desc = "يعكس حروف كل الرسائل المكتوبة ليصبح قراءتها لغزاً مضحكاً!",
                            isActive = isMirrorReverseActive,
                            activeLabel = "الكتابة معكوسة بالمرآة 🪞",
                            onClick = onToggleMirrorReverse
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 8. مقلب الجاذبية المعكوسة
                        PrankToggleCard(
                            emoji = "🙃",
                            title = "الجاذبية المعكوسة (قلب الشاشة 180°)",
                            desc = "يقلب شاشة الغرفة رأساً على عقب لجميع الحاضرين!",
                            isActive = isUpsideDownActive,
                            activeLabel = "الجاذبية مقلوبة الآن! 😂",
                            onClick = onToggleUpsideDown
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 9. مقلب لغة القطط
                        PrankToggleCard(
                            emoji = "🐱",
                            title = "تحويل الشات للغة القطط (مياوو)",
                            desc = "يحول الرسائل إلى مواء قطط مضحك!",
                            isActive = isCatLanguageActive,
                            activeLabel = "لغة القطط مفعلة 🐾",
                            onClick = onToggleCatLanguage
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 10. مقلب حفلة الديسكو
                        PrankToggleCard(
                            emoji = "🪩",
                            title = "حفلة الديسكو المجنونة",
                            desc = "وميض ألوان ديسكو مبهجة تملأ خلفية الغرفة!",
                            isActive = isDiscoActive,
                            activeLabel = "الديسكو مشعلل 🔥",
                            onClick = onToggleDisco
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 11. مقلب الطرد الوهمي الكوميدي
                        PrankActionCard(
                            emoji = "👻",
                            title = "مقلب الطرد الوهمي (Fake Kick)",
                            desc = "يظهر نافذة رعب كوميدي للأعضاء: 'تم طردك بواسطة معتز بسبب خفة دمك!'",
                            actionText = "إطلاق الطرد الوهمي 🤣",
                            actionColor = Color(0xFFFF5252),
                            onClick = onTriggerFakeKickPrank
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 12. مقلب تحويل الكل لبطاطس
                        PrankActionCard(
                            emoji = "🥔",
                            title = "تحويل الكل لبطاطس مقرمشة",
                            desc = "يمنح جميع المتواجدين في الغرفة أفاتار 'البطاطس الملوكية'!",
                            actionText = "تحويل لبطاطس 🥔",
                            actionColor = EgyptooSecondary,
                            onClick = onTriggerPotatoMorph
                        )
                    }

                    // SECTION 1: لوحة أصوات الميمز الفورية
                    1 -> {
                        Text(
                            text = "اضغط على أي صوت ليتم تشغيله فورياً وبثه لجميع الحاضرين في الغرفة:",
                            color = EgyptooTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            MemeSoundType.values().forEach { sound ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onPlayMemeSound(sound) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = EgyptooDarkCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = sound.emoji, fontSize = 22.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = sound.titleAr,
                                                color = EgyptooTextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(EgyptooPrimary.copy(alpha = 0.2f))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EgyptooPrimary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تشغيل 🔊", color = EgyptooPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 2: صلاحيات ملكية، بث لكل الغرف، ودخول مهيب
                    2 -> {
                        // الدخول المهيب الفخم
                        PrankActionCard(
                            emoji = "⚜️",
                            title = "إطلاق الدخول المهيب الملكي فورياً",
                            desc = "إعلان دخول رسمي مع موسيقى البوق الملكي وشعار ذهبي فخم في الغرفة!",
                            actionText = "تنفيذ الدخول المهيب 👑",
                            actionColor = EgyptooGold,
                            onClick = onTriggerGrandEntrance
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // مطر الجواهر والهدايا
                        PrankActionCard(
                            emoji = "💎",
                            title = "إمطار الغرفة بالجواهر والهدايا",
                            desc = "إسقاط قطع ألماس وعملات ذهبية تسقط بأنيميشن سلس لجميع المتواجدين!",
                            actionText = "إطلاق مطر الهدايا 🎁",
                            actionColor = EgyptooCyan,
                            onClick = onTriggerGiftRain
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // وضع التخفي والشبح
                        PrankToggleCard(
                            emoji = "🛡️",
                            title = "وضع الشبح والتخفي (Ghost Mode)",
                            desc = "الدخول والمشاهدة بدون إشعار دخول وبدون الظهور في قائمة الأعضاء.",
                            isActive = isGhostModeActive,
                            activeLabel = "وضع الشبح نشط (أنت مخفي) 👻",
                            onClick = onToggleGhostMode
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // تجميد الشات
                        PrankToggleCard(
                            emoji = "❄️",
                            title = "تجميد الشات / إجبار الصمت",
                            desc = "منع الأعضاء من إرسال رسائل جديدة مؤقتاً، فقط المشرف معتز يستطيع الكتابة.",
                            isActive = isRoomFrozen,
                            activeLabel = "الشات مجمد حالياً 🔒",
                            onClick = onToggleFreezeRoom
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // ميزة إرسال رسائل لكل المجموعات والغرف (البرقية الملكية لجميع الغرف)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E150A),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EgyptooGold)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = EgyptooGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "إرسال برقية لجميع الغرف والمجموعات 🌐",
                                        color = EgyptooGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Text(
                                    text = "تكتب رسالة واحدة، وتصل فورياً كرسالة ملكية في كل الغرف والمجموعات وتبقى مثبتة!",
                                    color = EgyptooTextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                OutlinedTextField(
                                    value = allRoomsBroadcastText,
                                    onValueChange = { allRoomsBroadcastText = it },
                                    placeholder = { Text("اكتب رسالتك لجميع الغرف والمجموعات...", fontSize = 11.sp) },
                                    maxLines = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = EgyptooTextPrimary,
                                        unfocusedTextColor = EgyptooTextPrimary,
                                        focusedBorderColor = EgyptooGold,
                                        unfocusedBorderColor = EgyptooBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (allRoomsBroadcastText.isNotBlank()) {
                                            onSendBroadcastToAllRoomsAndGroups(allRoomsBroadcastText)
                                            allRoomsBroadcastText = ""
                                        }
                                    },
                                    enabled = allRoomsBroadcastText.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("إرسال البرقية لكل الغرف دفعة واحدة 👑🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // الإذاعة الملكية العاجلة في شريط علوي
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = EgyptooDarkCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooSecondary)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "إذاعة إنذار ملكي عاجل (Global Alert)",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "رسالة تظهر في شريط ذهبي متحرك أعلى شاشات كل المستخدمين في التطبيق:",
                                    color = EgyptooTextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                OutlinedTextField(
                                    value = broadcastText,
                                    onValueChange = { broadcastText = it },
                                    placeholder = { Text("اكتب رسالة الإذاعة العامة هنا...", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = EgyptooTextPrimary,
                                        unfocusedTextColor = EgyptooTextPrimary,
                                        focusedBorderColor = EgyptooSecondary,
                                        unfocusedBorderColor = EgyptooBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (broadcastText.isNotBlank()) {
                                            onSendGlobalBroadcast(broadcastText)
                                            broadcastText = ""
                                        }
                                    },
                                    enabled = broadcastText.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("بث الرسالة للجميع فورياً 📢", color = Color.Black, fontWeight = FontWeight.Bold)
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
private fun PrankTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) EgyptooSecondary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else EgyptooTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun PrankToggleCard(
    emoji: String,
    title: String,
    desc: String,
    isActive: Boolean,
    activeLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) EgyptooSecondary.copy(alpha = 0.15f) else EgyptooDarkCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) EgyptooSecondary else EgyptooBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isActive) activeLabel else desc,
                        color = if (isActive) EgyptooSecondary else EgyptooTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Switch(
                checked = isActive,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EgyptooSecondary,
                    checkedTrackColor = EgyptooSecondary.copy(alpha = 0.4f),
                    uncheckedThumbColor = EgyptooTextSecondary,
                    uncheckedTrackColor = EgyptooBorder
                )
            )
        }
    }
}

@Composable
private fun PrankActionCard(
    emoji: String,
    title: String,
    desc: String,
    actionText: String,
    actionColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EgyptooDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                color = EgyptooTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = actionText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
