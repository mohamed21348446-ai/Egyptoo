package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.crypto.CryptoManager
import com.example.data.dao.ChatDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatRoomEntity
import com.example.data.model.MessageDeliveryStatus
import com.example.data.model.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ChatMessageEntity::class, ChatRoomEntity::class],
    version = 3,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getInstance(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "egyptoo_chat.db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { seedDatabase(it.chatDao()) }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(dao: ChatDao) {
            val mainEgyptRoomId = "room_egypt_public"
            val gamingRoomId = "room_arab_gaming"
            val saudiVibesRoomId = "room_saudi_public"
            val privateVipRoomId = "room_moataz_private"
            val moroccoRoomId = "room_morocco_public"
            val dmSarahId = "dm_sarah_designer"

            val rooms = listOf(
                ChatRoomEntity(
                    id = mainEgyptRoomId,
                    name = "ملتقى مصر أم الدنيا 🇪🇬🔥",
                    description = "غرفة عامة كبرى لشباب مصر - سوالف، بث مباشر، ونغمات طرب",
                    isGroup = true,
                    isPrivate = false,
                    countryCode = "EG",
                    countryFlag = "🇪🇬",
                    memberCount = 1420,
                    lastMessage = "🎙️ البث الصوتي المباشر مشتعل الآن مع معتز والأصدقاء!",
                    lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 2,
                    unreadCount = 3,
                    isLiveVoiceActive = true,
                    activeSpeakerCount = 4,
                    activeMusicTrackTitle = "ليالي النيون - Lofi Beat",
                    isE2EEVerified = true,
                    avatarInitials = "🇪🇬",
                    category = "عام وترفيه"
                ),
                ChatRoomEntity(
                    id = privateVipRoomId,
                    name = "غرفة VIP معتز الخاصة 👑🔒",
                    description = "غرفة خاصة مشفرة للأصدقاء المقربين - تتطلب رمز دخول PIN",
                    isGroup = true,
                    isPrivate = true,
                    accessPin = "2026",
                    countryCode = "EG",
                    countryFlag = "🇪🇬",
                    memberCount = 6,
                    lastMessage = "🔒 رمز الأمان مفعل، أهلاً بالأعضاء المقربين من المشرف معتز",
                    lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                    unreadCount = 1,
                    isLiveVoiceActive = true,
                    activeSpeakerCount = 2,
                    activeMusicTrackTitle = "ألحان النيل الهادئة",
                    isE2EEVerified = true,
                    avatarInitials = "👑",
                    category = "خاصة VIP"
                ),
                ChatRoomEntity(
                    id = gamingRoomId,
                    name = "حلبة التحديات والألعاب 🎮⚔️",
                    description = "مسابقات سرعة البديهة، X-O، وعجلة التحديات لكل العرب",
                    isGroup = true,
                    isPrivate = false,
                    countryCode = "ALL",
                    countryFlag = "🌐",
                    memberCount = 890,
                    lastMessage = "🏆 فاز التحدي الأخير بـ 40 نقطة! من يلعب جولة X-O جديدة؟",
                    lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                    unreadCount = 0,
                    isLiveVoiceActive = false,
                    activeSpeakerCount = 0,
                    activeMusicTrackTitle = "",
                    isE2EEVerified = true,
                    avatarInitials = "🎮",
                    category = "ألعاب وتحديات"
                ),
                ChatRoomEntity(
                    id = saudiVibesRoomId,
                    name = "مجلس الخليج والسعودية 🇸🇦✨",
                    description = "غرفة عامة لأهل السعودية والخليج - حوارات وبودكاست حي",
                    isGroup = true,
                    isPrivate = false,
                    countryCode = "SA",
                    countryFlag = "🇸🇦",
                    memberCount = 670,
                    lastMessage = "يا هلا بالجميع! شغلوا لنا جلسة طرب خليجي في المشغل",
                    lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    unreadCount = 0,
                    isLiveVoiceActive = false,
                    activeSpeakerCount = 0,
                    activeMusicTrackTitle = "",
                    isE2EEVerified = true,
                    avatarInitials = "🇸🇦",
                    category = "مجالس وثقافة"
                ),
                ChatRoomEntity(
                    id = moroccoRoomId,
                    name = "صالون المغرب والجزائر 🇲🇦🇩🇿",
                    description = "غرفة الأشقاء في المغرب والجزائر وتونس - تواصل وموسيقى",
                    isGroup = true,
                    isPrivate = false,
                    countryCode = "MA",
                    countryFlag = "🇲🇦",
                    memberCount = 420,
                    lastMessage = "مرحبا بالجميع! أجواء حماسية في البث الصوتي",
                    lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                    unreadCount = 0,
                    isLiveVoiceActive = false,
                    activeSpeakerCount = 0,
                    activeMusicTrackTitle = "",
                    isE2EEVerified = true,
                    avatarInitials = "🇲🇦",
                    category = "تواصل"
                ),
                ChatRoomEntity(
                    id = dmSarahId,
                    name = "سارة المنصور 🎨",
                    description = "متصل الآن",
                    isGroup = false,
                    isPrivate = true,
                    countryCode = "ALL",
                    countryFlag = "🌐",
                    memberCount = 2,
                    lastMessage = "🔒 الأمان التام مفعل بيننا. اسمع مقطع اللحن اللي جهزته لتطبيق Egyptoo!",
                    lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                    unreadCount = 0,
                    isLiveVoiceActive = false,
                    activeSpeakerCount = 0,
                    activeMusicTrackTitle = "",
                    isE2EEVerified = true,
                    avatarInitials = "س",
                    category = "محادثة خاصة"
                )
            )
            dao.insertRooms(rooms)

            val now = System.currentTimeMillis()
            val initialMessages = listOf(
                ChatMessageEntity(
                    roomId = mainEgyptRoomId,
                    senderId = "system",
                    senderName = "النظام",
                    content = "🇪🇬 مرحباً بكم في تطبيق Egyptoo! المحادثات والغرف محمية بتشفير تام E2EE وبثوث صوتية تفاعلية.",
                    timestamp = now - 1000 * 60 * 30,
                    type = MessageType.SYSTEM_E2EE,
                    status = MessageDeliveryStatus.READ,
                    encryptedPayload = CryptoManager.encrypt("E2EE_EGYPTOO_KEY_EXCHANGE_OK", mainEgyptRoomId)
                ),
                ChatMessageEntity(
                    roomId = mainEgyptRoomId,
                    senderId = "user_omar",
                    senderName = "عمر الفهد ⚡",
                    content = "منورين يا شباب في Egyptoo! تطبيق فخم وألوانه تفتح النفس، معتز المشرف هنا منور الغرفة 👑",
                    timestamp = now - 1000 * 60 * 25,
                    type = MessageType.TEXT,
                    status = MessageDeliveryStatus.READ,
                    encryptedPayload = CryptoManager.encrypt("منورين يا شباب في Egyptoo! تطبيق فخم", mainEgyptRoomId)
                ),
                ChatMessageEntity(
                    roomId = mainEgyptRoomId,
                    senderId = "user_sarah",
                    senderName = "سارة المنصور 🎨",
                    content = "أهلاً بمعتز وأهلاً بالجميع! البث الصوتي صوته نقي جداً، شغلت المايك معكم 🎙️",
                    timestamp = now - 1000 * 60 * 20,
                    type = MessageType.TEXT,
                    status = MessageDeliveryStatus.READ,
                    encryptedPayload = CryptoManager.encrypt("أهلاً بمعتز وأهلاً بالجميع! البث الصوتي صوته نقي جداً", mainEgyptRoomId)
                ),
                ChatMessageEntity(
                    roomId = mainEgyptRoomId,
                    senderId = "user_moataz",
                    senderName = "معتز 👑",
                    content = "أهلاً بكل أصدقاء Egyptoo! لوحة الإشراف والمجموعات والبثوث كلها جاهزة، استمتعوا معنا 🔥",
                    timestamp = now - 1000 * 60 * 10,
                    type = MessageType.TEXT,
                    status = MessageDeliveryStatus.READ,
                    encryptedPayload = CryptoManager.encrypt("أهلاً بكل أصدقاء Egyptoo! استمتعوا معنا 🔥", mainEgyptRoomId),
                    isFromMe = true
                ),
                ChatMessageEntity(
                    roomId = mainEgyptRoomId,
                    senderId = "user_omar",
                    senderName = "عمر الفهد ⚡",
                    content = "🎵 استمعوا معاً: ليالي النيون (Lofi Beat)",
                    timestamp = now - 1000 * 60 * 5,
                    type = MessageType.MUSIC_TRACK,
                    status = MessageDeliveryStatus.READ,
                    encryptedPayload = CryptoManager.encrypt("TRACK: ليالي النيون", mainEgyptRoomId),
                    gamePayload = "ليالي النيون - Lofi Beat"
                )
            )

            initialMessages.forEach { dao.insertMessage(it) }
        }
    }
}
