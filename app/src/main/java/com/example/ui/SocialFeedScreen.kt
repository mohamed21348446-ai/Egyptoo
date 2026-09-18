package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
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
import com.example.data.model.SocialPost
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SocialFeedScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.socialPosts.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var newPostContent by remember { mutableStateOf("") }
    var selectedFeeling by remember { mutableStateOf("متحمس وفخور 🚀") }
    var isPostingBoxExpanded by remember { mutableStateOf(false) }

    val feelingsList = listOf(
        "متحمس وفخور 🚀",
        "يستمع إلى الموسيقى 🎵",
        "في تحدي ألعاب 🎮",
        "في مصر 🇪🇬",
        "سعيد جداً 😊",
        "يسولف في الغرفة 🎙️"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("social_feed_screen"),
        containerColor = EgyptooDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Create Post Header Card (زي الفيس بوك)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = EgyptooDarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EgyptooSecondary.copy(alpha = 0.2f))
                                    .border(1.5.dp, EgyptooSecondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(uiState.userAvatar, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EgyptooDarkBg)
                                    .clickable { isPostingBoxExpanded = !isPostingBoxExpanded }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "ماذا يدور في ذهنك يا معتز؟ ✍️",
                                    color = EgyptooTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        AnimatedVisibility(visible = isPostingBoxExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                OutlinedTextField(
                                    value = newPostContent,
                                    onValueChange = { newPostContent = it },
                                    placeholder = { Text("شارك منشورك، أفكارك، أو تحدياتك مع أعضاء Egyptoo...", fontSize = 13.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 4,
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

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("حدد الحالة أو الشعور:", color = EgyptooTextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(feelingsList) { feeling ->
                                        val isSelected = selectedFeeling == feeling
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) EgyptooPrimary else EgyptooDarkBg)
                                                .clickable { selectedFeeling = feeling }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = feeling,
                                                color = if (isSelected) Color.White else EgyptooTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            if (newPostContent.isNotBlank()) {
                                                viewModel.createPost(newPostContent, selectedFeeling)
                                                newPostContent = ""
                                                isPostingBoxExpanded = false
                                            }
                                        },
                                        enabled = newPostContent.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EgyptooPrimary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("نشر الآن 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Feed Stream Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📰", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "منشورات مجتمع Egyptoo",
                            color = EgyptooTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Badge(containerColor = EgyptooPrimary, contentColor = Color.White) {
                        Text("${posts.size} منشور", fontSize = 10.sp, modifier = Modifier.padding(2.dp))
                    }
                }
            }

            // 3. Posts List
            items(posts, key = { it.id }) { post ->
                PostCardItem(
                    post = post,
                    onReact = { reaction -> viewModel.togglePostReaction(post.id, reaction) },
                    onAddComment = { comment -> viewModel.addPostComment(post.id, comment) }
                )
            }
        }
    }
}

@Composable
private fun PostCardItem(
    post: SocialPost,
    onReact: (String) -> Unit,
    onAddComment: (String) -> Unit
) {
    var showComments by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }

    val formattedTime = remember(post.timestamp) {
        val sdf = SimpleDateFormat("h:mm a • d MMM", Locale.getDefault())
        sdf.format(Date(post.timestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        color = EgyptooDarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, EgyptooBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Post Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (post.authorRole == "OWNER_ADMIN") EgyptooSecondary.copy(alpha = 0.25f)
                                else EgyptooDarkBg
                            )
                            .border(
                                1.5.dp,
                                if (post.authorRole == "OWNER_ADMIN") EgyptooSecondary else EgyptooBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(post.authorAvatar, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                color = if (post.authorRole == "OWNER_ADMIN") EgyptooSecondary else EgyptooTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.authorRole == "OWNER_ADMIN") {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = "مشرف عام", tint = EgyptooSecondary, modifier = Modifier.size(15.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = post.authorCountryFlag, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = formattedTime, color = EgyptooTextSecondary, fontSize = 10.sp)
                            if (post.feeling.isNotEmpty()) {
                                Text(text = " • يشعر بـ ${post.feeling}", color = EgyptooCyan, fontSize = 10.sp)
                            }
                        }
                    }
                }

                if (post.authorRole == "OWNER_ADMIN") {
                    Badge(containerColor = EgyptooSecondary, contentColor = Color.Black) {
                        Text("المالك معتز 👑", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Content Text
            Text(
                text = post.content,
                color = EgyptooTextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reaction Counters summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "❤️ 🔥 👍", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${post.likesCount}", color = EgyptooTextSecondary, fontSize = 12.sp)
                }

                Text(
                    text = "${post.comments.size} تعليقات",
                    color = EgyptooTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { showComments = !showComments }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EgyptooBorder))
            Spacer(modifier = Modifier.height(6.dp))

            // Facebook Style Action Bar (Like, Love, Haha, Fire, Comments)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / Love Button
                val hasReacted = post.userReaction != null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onReact(if (post.userReaction == "LOVE") "LIKE" else "LOVE")
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (post.userReaction) {
                                "LOVE" -> "❤️"
                                "LIKE" -> "👍"
                                "FIRE" -> "🔥"
                                "HAHA" -> "😂"
                                else -> "🤍"
                            },
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasReacted) "تفاعلت" else "تفاعل",
                            color = if (hasReacted) EgyptooTertiary else EgyptooTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (hasReacted) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Fire Reaction
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onReact("FIRE") }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حماس", color = EgyptooTextSecondary, fontSize = 12.sp)
                    }
                }

                // Comments Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showComments = !showComments }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = EgyptooTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعليق", color = EgyptooTextSecondary, fontSize = 12.sp)
                    }
                }

                // Share Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { /* Simulated Share */ }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = EgyptooTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مشاركة", color = EgyptooTextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Comments Section (Expandable)
            AnimatedVisibility(visible = showComments) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Previous Comments
                    post.comments.forEach { comment ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EgyptooDarkBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = comment.authorName,
                                    color = EgyptooCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = comment.content,
                                    color = EgyptooTextPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Add Comment Row
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = { Text("اكتب تعليقك كـ معتز 👑...", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = EgyptooTextPrimary,
                                unfocusedTextColor = EgyptooTextPrimary,
                                focusedContainerColor = EgyptooDarkBg,
                                unfocusedContainerColor = EgyptooDarkBg,
                                focusedBorderColor = EgyptooPrimary,
                                unfocusedBorderColor = EgyptooBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    onAddComment(commentInput)
                                    commentInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EgyptooPrimary)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
