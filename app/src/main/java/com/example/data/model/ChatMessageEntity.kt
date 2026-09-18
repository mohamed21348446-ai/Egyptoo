package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageType {
    TEXT,
    VOICE_NOTE,
    GAME_INVITE,
    GAME_RESULT,
    MUSIC_TRACK,
    SYSTEM_E2EE
}

enum class MessageDeliveryStatus {
    SENT,
    DELIVERED,
    READ
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String = "",
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT,
    val status: MessageDeliveryStatus = MessageDeliveryStatus.READ,
    val encryptedPayload: String = "",
    val isFromMe: Boolean = false,
    val audioDurationSec: Int = 0,
    val gamePayload: String = "", // For game state or track title
    val senderGender: String = "MALE", // "MALE" or "FEMALE"
    val senderAge: Int = 24,
    val isGrandEntrance: Boolean = false,
    val isGlobalBroadcast: Boolean = false,
    val isOrnateRoyal: Boolean = false
)
