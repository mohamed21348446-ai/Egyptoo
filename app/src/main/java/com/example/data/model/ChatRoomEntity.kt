package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_rooms")
data class ChatRoomEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val isGroup: Boolean = true,
    val isPrivate: Boolean = false,
    val accessPin: String = "",
    val countryCode: String = "EG",
    val countryFlag: String = "🇪🇬",
    val memberCount: Int = 4,
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isLiveVoiceActive: Boolean = false,
    val activeSpeakerCount: Int = 0,
    val activeMusicTrackTitle: String = "",
    val isE2EEVerified: Boolean = true,
    val avatarInitials: String = "🇪🇬",
    val isFrozenByAdmin: Boolean = false,
    val category: String = "عام"
)
