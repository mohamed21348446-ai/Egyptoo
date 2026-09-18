package com.example.data.repository

import com.example.crypto.CryptoManager
import com.example.data.dao.ChatDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatRoomEntity
import com.example.data.model.MessageDeliveryStatus
import com.example.data.model.MessageType
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {

    val currentUserName: String = "معتز"
    val currentUserEmail: String = "mohamed21348446@gmail.com"

    fun getAllRooms(): Flow<List<ChatRoomEntity>> = chatDao.getAllRooms()

    fun getRoom(roomId: String): Flow<ChatRoomEntity?> = chatDao.getRoomById(roomId)

    fun getMessages(roomId: String): Flow<List<ChatMessageEntity>> = chatDao.getMessagesForRoom(roomId)

    suspend fun sendMessage(
        roomId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        gamePayload: String = "",
        audioDuration: Int = 0,
        senderName: String = "معتز 👑",
        senderAvatar: String = "👑",
        senderGender: String = "MALE",
        senderAge: Int = 24,
        isOrnateRoyal: Boolean = false,
        isGlobalBroadcast: Boolean = false
    ) {
        val encryptedPayload = CryptoManager.encrypt(content, roomId)
        val message = ChatMessageEntity(
            roomId = roomId,
            senderId = "user_moataz",
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            timestamp = System.currentTimeMillis(),
            type = type,
            status = MessageDeliveryStatus.READ,
            encryptedPayload = encryptedPayload,
            isFromMe = true,
            audioDurationSec = audioDuration,
            gamePayload = gamePayload,
            senderGender = senderGender,
            senderAge = senderAge,
            isOrnateRoyal = isOrnateRoyal,
            isGlobalBroadcast = isGlobalBroadcast
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(roomId, content, System.currentTimeMillis())
    }

    suspend fun sendBroadcastToAllRooms(
        rooms: List<ChatRoomEntity>,
        content: String,
        senderName: String = "معتز 👑",
        senderAvatar: String = "👑",
        senderGender: String = "MALE",
        senderAge: Int = 24
    ) {
        val now = System.currentTimeMillis()
        rooms.forEach { room ->
            val encryptedPayload = CryptoManager.encrypt(content, room.id)
            val message = ChatMessageEntity(
                roomId = room.id,
                senderId = "user_moataz",
                senderName = senderName,
                senderAvatar = senderAvatar,
                content = content,
                timestamp = now,
                type = MessageType.TEXT,
                status = MessageDeliveryStatus.READ,
                encryptedPayload = encryptedPayload,
                isFromMe = true,
                senderGender = senderGender,
                senderAge = senderAge,
                isGlobalBroadcast = true,
                isOrnateRoyal = true
            )
            chatDao.insertMessage(message)
            chatDao.updateLastMessage(room.id, "👑 برقية ملكية: $content", now)
        }
    }

    suspend fun sendGrandEntranceAnnouncement(
        roomId: String,
        senderName: String = "معتز 👑",
        senderAvatar: String = "👑",
        senderGender: String = "MALE",
        senderAge: Int = 24
    ) {
        val entranceText = "👑 ⚜️ حضرة المشرف العام والمالك معتز شرف الغرفة بدخول مهيب وفخامة ملكية! ⚜️ 👑"
        val encryptedPayload = CryptoManager.encrypt(entranceText, roomId)
        val message = ChatMessageEntity(
            roomId = roomId,
            senderId = "user_moataz",
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = entranceText,
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            status = MessageDeliveryStatus.READ,
            encryptedPayload = encryptedPayload,
            isFromMe = true,
            senderGender = senderGender,
            senderAge = senderAge,
            isGrandEntrance = true,
            isOrnateRoyal = true
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(roomId, entranceText, System.currentTimeMillis())
    }

    suspend fun insertBotReply(
        roomId: String,
        senderName: String,
        reply: String,
        type: MessageType = MessageType.TEXT,
        senderAvatar: String = "⚡",
        senderGender: String = "MALE",
        senderAge: Int = 24
    ) {
        val encryptedPayload = CryptoManager.encrypt(reply, roomId)
        val message = ChatMessageEntity(
            roomId = roomId,
            senderId = "bot_${senderName.hashCode()}",
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = reply,
            timestamp = System.currentTimeMillis(),
            type = type,
            status = MessageDeliveryStatus.READ,
            encryptedPayload = encryptedPayload,
            isFromMe = false,
            senderGender = senderGender,
            senderAge = senderAge
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(roomId, "$senderName: $reply", System.currentTimeMillis())
    }

    suspend fun setLiveVoiceBroadcast(roomId: String, isActive: Boolean, speakers: Int) {
        chatDao.updateVoiceBroadcastStatus(roomId, isActive, speakers)
    }

    suspend fun setActiveMusicTrack(roomId: String, trackTitle: String) {
        chatDao.updateMusicStatus(roomId, trackTitle)
    }

    suspend fun createRoom(room: ChatRoomEntity) {
        chatDao.insertRoom(room)
    }

    suspend fun freezeRoom(roomId: String, isFrozen: Boolean) {
        chatDao.updateFreezeStatus(roomId, isFrozen)
    }

    suspend fun deleteRoom(roomId: String) {
        chatDao.deleteRoom(roomId)
        chatDao.clearMessages(roomId)
    }
}
