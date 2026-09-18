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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountriesProvider
import com.example.ui.theme.EgyptooBorder
import com.example.ui.theme.EgyptooDarkBg
import com.example.ui.theme.EgyptooDarkCard
import com.example.ui.theme.EgyptooPrimary
import com.example.ui.theme.EgyptooSecondary
import com.example.ui.theme.EgyptooTextPrimary
import com.example.ui.theme.EgyptooTextSecondary

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreateRoom: (name: String, description: String, isPrivate: Boolean, pin: String, countryCode: String, flag: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(CountriesProvider.supportedCountries[1]) } // Egypt
    var category by remember { mutableStateOf("عام وترفيه") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptooDarkCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إنشاء غرفة جديدة في Egyptoo 🎙️",
                    color = EgyptooTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EgyptooTextSecondary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Room Type Selector (عامة / خاصة)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EgyptooDarkBg)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (!isPrivate) EgyptooPrimary else Color.Transparent)
                            .clickable { isPrivate = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("غرفة عامة 🌍", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isPrivate) EgyptooSecondary else Color.Transparent)
                            .clickable { isPrivate = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("غرفة خاصة 🔒", color = if (isPrivate) Color.Black else EgyptooTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الغرفة", fontSize = 12.sp) },
                    placeholder = { Text("مثال: مجلس سوالف مصر 🇪🇬", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = EgyptooTextPrimary,
                        unfocusedTextColor = EgyptooTextPrimary,
                        focusedContainerColor = EgyptooDarkBg,
                        unfocusedContainerColor = EgyptooDarkBg,
                        focusedBorderColor = EgyptooPrimary,
                        unfocusedBorderColor = EgyptooBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف أو نبذة عن الغرفة", fontSize = 12.sp) },
                    placeholder = { Text("غرفة حوارية وبث صوتي للموسيقى والألعاب", fontSize = 12.sp) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = EgyptooTextPrimary,
                        unfocusedTextColor = EgyptooTextPrimary,
                        focusedContainerColor = EgyptooDarkBg,
                        unfocusedContainerColor = EgyptooDarkBg,
                        focusedBorderColor = EgyptooPrimary,
                        unfocusedBorderColor = EgyptooBorder
                    )
                )

                if (isPrivate) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 6) pin = it },
                        label = { Text("رمز الدخول السري (PIN)", fontSize = 12.sp) },
                        placeholder = { Text("مثال: 1234", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = EgyptooTextPrimary,
                            unfocusedTextColor = EgyptooTextPrimary,
                            focusedContainerColor = EgyptooDarkBg,
                            unfocusedContainerColor = EgyptooDarkBg,
                            focusedBorderColor = EgyptooSecondary,
                            unfocusedBorderColor = EgyptooBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Country Flag Selector
                Text("حدد علم الدولة المرتبطة بالغرفة:", color = EgyptooTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CountriesProvider.supportedCountries.drop(1)) { country ->
                        val isSelected = country.code == selectedCountry.code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EgyptooPrimary.copy(alpha = 0.3f) else EgyptooDarkBg)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) EgyptooSecondary else EgyptooBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCountry = country }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("${country.flag} ${country.nameAr}", fontSize = 12.sp, color = EgyptooTextPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateRoom(
                        name,
                        description,
                        isPrivate,
                        pin,
                        selectedCountry.code,
                        selectedCountry.flag,
                        category
                    )
                },
                enabled = name.isNotBlank() && (!isPrivate || pin.isNotBlank()),
                colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إنشاء الغرفة والدخول 🔥", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إلغاء", color = EgyptooTextSecondary)
            }
        }
    )
}
