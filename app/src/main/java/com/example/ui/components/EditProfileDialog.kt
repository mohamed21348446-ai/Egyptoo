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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserGender
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooCyan
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooPurple
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary
import kotlin.math.roundToInt

@Composable
fun EditProfileDialog(
    initialName: String,
    initialAvatar: String,
    initialGender: UserGender,
    initialAge: Int,
    initialBio: String,
    onSaveProfile: (name: String, avatar: String, gender: UserGender, age: Int, bio: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var avatar by remember { mutableStateOf(initialAvatar) }
    var gender by remember { mutableStateOf(initialGender) }
    var ageFloat by remember { mutableFloatStateOf(initialAge.toFloat().coerceIn(14f, 80f)) }
    var bio by remember { mutableStateOf(initialBio) }

    val presetAvatars = listOf(
        "👑", "⚡", "🎨", "🚀", "🌸", "⭐", "🦁", "💎", "🎮", "🎧", "🔥", "🦄", "🦅", "🐱", "🐶", "🌺"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = EgyptooDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, EgyptooSecondary),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تعديل الملف الشخصي ✏️",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EgyptooTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Selection
                Text(
                    text = "اختر صورة البروفايل أو الأفاتار:",
                    color = EgyptooTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetAvatars) { item ->
                        val isSelected = avatar == item
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EgyptooSecondary.copy(alpha = 0.25f) else EgyptooDarkCard)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) EgyptooSecondary else EgyptooBorder,
                                    shape = CircleShape
                                )
                                .clickable { avatar = item },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name Input
                Text(
                    text = "الاسم المعروض:",
                    color = EgyptooTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("اكتب اسمك هنا...", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = EgyptooTextPrimary,
                        unfocusedTextColor = EgyptooTextPrimary,
                        focusedBorderColor = EgyptooSecondary,
                        unfocusedBorderColor = EgyptooBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // GENDER SELECTOR (ولد / بنت)
                Text(
                    text = "الجنس (يظهر بجوار اسمك في كل الغرف):",
                    color = EgyptooTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Male Option (ولد 👦)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { gender = UserGender.MALE }
                            .testTag("gender_male_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (gender == UserGender.MALE) Color(0xFF0D47A1).copy(alpha = 0.4f) else EgyptooDarkCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (gender == UserGender.MALE) 2.dp else 1.dp,
                            color = if (gender == UserGender.MALE) EgyptooCyan else EgyptooBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Male, contentDescription = null, tint = EgyptooCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ولد 👦",
                                color = if (gender == UserGender.MALE) EgyptooCyan else EgyptooTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Female Option (بنت 👧)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { gender = UserGender.FEMALE }
                            .testTag("gender_female_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (gender == UserGender.FEMALE) Color(0xFF880E4F).copy(alpha = 0.4f) else EgyptooDarkCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (gender == UserGender.FEMALE) 2.dp else 1.dp,
                            color = if (gender == UserGender.FEMALE) Color(0xFFFF4081) else EgyptooBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Female, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بنت 👧",
                                color = if (gender == UserGender.FEMALE) Color(0xFFFF80AB) else EgyptooTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AGE SELECTOR (واضح للكل)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cake, contentDescription = null, tint = EgyptooSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "العمر (واضح للكل):", color = EgyptooTextSecondary, fontSize = 12.sp)
                    }
                    Text(
                        text = "${ageFloat.roundToInt()} سنة 🎂",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Slider(
                    value = ageFloat,
                    onValueChange = { ageFloat = it },
                    valueRange = 14f..80f,
                    steps = 66,
                    colors = SliderDefaults.colors(
                        thumbColor = EgyptooSecondary,
                        activeTrackColor = EgyptooSecondary,
                        inactiveTrackColor = EgyptooBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bio / Status
                Text(
                    text = "الحالة / نبذة عني:",
                    color = EgyptooTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = { Text("اكتب نبذة أو حالتك اليومية...", fontSize = 12.sp) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = EgyptooTextPrimary,
                        unfocusedTextColor = EgyptooTextPrimary,
                        focusedBorderColor = EgyptooSecondary,
                        unfocusedBorderColor = EgyptooBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save button
                Button(
                    onClick = {
                        onSaveProfile(name, avatar, gender, ageFloat.roundToInt(), bio)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptooSecondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_profile_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ التغييرات في ملفي 🌟", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
