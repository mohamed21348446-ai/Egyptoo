package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_rooms ORDER BY lastMessageTimestamp DESC")
    fun getAllRooms(): Flow<List<ChatRoomEntity>>

    @Query("SELECT * FROM chat_rooms WHERE id = :roomId LIMIT 1")
    fun getRoomById(roomId: String): Flow<ChatRoomEntity?>

    @Query("SELECT * FROM chat_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: ChatRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<ChatRoomEntity>)

    @Update
    suspend fun updateRoom(room: ChatRoomEntity)

    @Query("UPDATE chat_rooms SET lastMessage = :lastMsg, lastMessageTimestamp = :time WHERE id = :roomId")
    suspend fun updateLastMessage(roomId: String, lastMsg: String, time: Long)

    @Query("UPDATE chat_rooms SET isLiveVoiceActive = :isActive, activeSpeakerCount = :speakerCount WHERE id = :roomId")
    suspend fun updateVoiceBroadcastStatus(roomId: String, isActive: Boolean, speakerCount: Int)

    @Query("UPDATE chat_rooms SET activeMusicTrackTitle = :trackTitle WHERE id = :roomId")
    suspend fun updateMusicStatus(roomId: String, trackTitle: String)

    @Query("UPDATE chat_rooms SET isFrozenByAdmin = :isFrozen WHERE id = :roomId")
    suspend fun updateFreezeStatus(roomId: String, isFrozen: Boolean)

    @Query("DELETE FROM chat_rooms WHERE id = :roomId")
    suspend fun deleteRoom(roomId: String)

    @Query("DELETE FROM chat_messages WHERE roomId = :roomId")
    suspend fun clearMessages(roomId: String)
}
