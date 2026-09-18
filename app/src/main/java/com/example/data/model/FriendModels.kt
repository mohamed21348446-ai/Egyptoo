package com.example.data.model

enum class UserGender(val labelAr: String, val emoji: String, val code: String) {
    MALE("ولد 👦", "👦", "MALE"),
    FEMALE("بنت 👧", "👧", "FEMALE");

    companion object {
        fun fromCode(code: String?): UserGender {
            return when (code?.uppercase()) {
                "FEMALE", "GIRL", "بنت" -> FEMALE
                else -> MALE
            }
        }
    }
}

data class FriendUser(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String,
    val countryFlag: String = "🇪🇬",
    val countryCode: String = "EG",
    val isOnline: Boolean = true,
    val statusMessage: String = "",
    val vipBadge: String = "عضو مميز ⭐",
    val egyptooCoins: Int = 500,
    val isPotatoMorphed: Boolean = false,
    val gender: UserGender = UserGender.MALE,
    val age: Int = 24,
    val bio: String = "عضو نشط في مجتمع وجروبات Egyptoo ✨",
    val isSuperAdmin: Boolean = false
)

data class FriendRequest(
    val id: String,
    val fromUser: FriendUser,
    val timestamp: Long = System.currentTimeMillis()
)

data class DirectMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val senderAvatar: String,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val audioDuration: Int = 0,
    val mediaEmoji: String? = null,
    val senderGender: UserGender = UserGender.MALE,
    val senderAge: Int = 24
)

data class FriendStory(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val authorFlag: String,
    val mediaEmoji: String,
    val caption: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val bgGradientColor: String = "PURPLE"
)

enum class AuthProvider {
    GOOGLE,
    FACEBOOK,
    NONE
}

data class AuthUser(
    val email: String,
    val displayName: String,
    val avatar: String,
    val provider: AuthProvider,
    val isSuperAdmin: Boolean = false,
    val gender: UserGender = UserGender.MALE,
    val age: Int = 24,
    val bio: String = ""
)
