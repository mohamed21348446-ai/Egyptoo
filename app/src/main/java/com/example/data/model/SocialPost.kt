package com.example.data.model

data class PostComment(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SocialPost(
    val id: String,
    val authorName: String,
    val authorEmail: String = "",
    val authorAvatar: String,
    val authorCountryFlag: String = "🇪🇬",
    val authorRole: String = "USER", // "OWNER_ADMIN", "VIP", "USER"
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    val feeling: String = "",
    val likesCount: Int = 0,
    val userReaction: String? = null, // "LIKE", "LOVE", "FIRE", "HAHA"
    val comments: List<PostComment> = emptyList(),
    val countryCode: String = "EG",
    val groupName: String? = null
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val countryCode: String = "ALL",
    val countryFlag: String = "🌐",
    val memberCount: Int = 1250,
    val isPrivate: Boolean = false,
    val isJoined: Boolean = false,
    val category: String = "عام",
    val recentActivity: String = ""
)

data class SupportTicket(
    val id: String,
    val userName: String,
    val userEmail: String,
    val subject: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, RESOLVED
    val adminReply: String? = null
)

data class BannedAccount(
    val email: String,
    val userName: String,
    val reason: String,
    val bannedAt: Long = System.currentTimeMillis()
)
