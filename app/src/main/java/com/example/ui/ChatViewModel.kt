package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.LiveVoiceBroadcastManager
import com.example.audio.MemeSoundEffectsPlayer
import com.example.audio.MemeSoundType
import com.example.audio.MusicSynthPlayer
import com.example.data.db.ChatDatabase
import com.example.data.model.AuthProvider
import com.example.data.model.BannedAccount
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatRoomEntity
import com.example.data.model.CommunityGroup
import com.example.data.model.CountriesProvider
import com.example.data.model.CountryItem
import com.example.data.model.DirectMessage
import com.example.data.model.FriendRequest
import com.example.data.model.FriendStory
import com.example.data.model.FriendUser
import com.example.data.model.MessageType
import com.example.data.model.PostComment
import com.example.data.model.SocialPost
import com.example.data.model.SupportTicket
import com.example.data.repository.ChatRepository
import com.example.games.GameType
import com.example.games.GamesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class EgyptooNavTab {
    CHATS_AND_ROOMS,
    SOCIAL_FEED,
    FRIENDS_AND_DMS,
    GROUPS,
    ADMIN_PANEL,
    PROFILE
}

data class ChatUiState(
    val currentRoomId: String = "room_egypt_public",
    val isInsideRoom: Boolean = false,
    val selectedNavTab: EgyptooNavTab = EgyptooNavTab.CHATS_AND_ROOMS,
    val inputText: String = "",
    val showEncryptedCiphertext: Boolean = false,
    val isVoiceStageExpanded: Boolean = true,
    val isMusicPlayerExpanded: Boolean = true,
    val showSecurityDialog: Boolean = false,
    val showGamesPicker: Boolean = false,
    val showMusicQueue: Boolean = false,
    val isRecordingVoiceNote: Boolean = false,
    val recordedDurationSec: Int = 0,
    val activeFriendTyping: String? = null,
    // Country & Language
    val selectedCountry: CountryItem = CountriesProvider.supportedCountries[1], // Default Egypt 🇪🇬
    val selectedLanguage: String = "العربية",
    val showCountryLanguageDialog: Boolean = false,
    // Room filters & creation
    val roomFilterTab: Int = 0, // 0 = الكل, 1 = عامة 🌍, 2 = خاصة 🔒
    val showCreateRoomDialog: Boolean = false,
    val privateRoomPinPromptId: String? = null,
    val privateRoomError: String? = null,
    // Mandatory Authentication State (Google / Facebook)
    val isAuthenticated: Boolean = true,
    val authProvider: AuthProvider = AuthProvider.GOOGLE,
    // Current user info (معتز 👑)
    val userName: String = "معتز",
    val userEmail: String = "mohamed21348446@gmail.com",
    val userAvatar: String = "👑",
    val userGender: com.example.data.model.UserGender = com.example.data.model.UserGender.MALE,
    val userAge: Int = 24,
    val userBio: String = "المشرف العام ومؤسس مجتمع Egyptoo 👑✨",
    val showEditProfileDialog: Boolean = false,
    val inspectedUserProfile: FriendUser? = null,
    // Admin features & Superpower Tricks
    val adminAnnouncement: String? = null,
    val adminFeedbackMessage: String? = null,
    val showPrankArsenalDialog: Boolean = false,
    val isUpsideDownPrankActive: Boolean = false,
    val isDiscoPrankActive: Boolean = false,
    val isCatLanguagePrankActive: Boolean = false,
    val isGhostModeActive: Boolean = false,
    val fakeKickPrankDialogActive: Boolean = false,
    val giftRainCount: Int = 0,
    val globalBroadcastAlert: String? = null,
    // New Grand Entrance & Expanded Prank States
    val isGrandEntranceActive: Boolean = false,
    val grandEntranceTitle: String? = null,
    val isEarthquakeActive: Boolean = false,
    val isTomatoSplatActive: Boolean = false,
    val fakeBatteryPrankActive: Boolean = false,
    val isMatrixHackerActive: Boolean = false,
    val isHeliumAlienVoiceActive: Boolean = false,
    val isSlowMoTurtleActive: Boolean = false,
    val isHeliumVoiceActive: Boolean = false,
    val isSlowMoActive: Boolean = false,
    val isMirrorReverseActive: Boolean = false,
    val prankTargetAllRooms: Boolean = false, // false = الغرفة الحالية فقط, true = جميع الغرف والمجموعات
    // 1-on-1 Direct Chat navigation
    val selectedDirectFriend: FriendUser? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ChatDatabase.getInstance(application)
    private val repository = ChatRepository(database.chatDao())

    val musicPlayer = MusicSynthPlayer(viewModelScope)
    val voiceBroadcast = LiveVoiceBroadcastManager(application, viewModelScope)
    val gamesManager = GamesManager(viewModelScope)
    val memeSoundsPlayer = MemeSoundEffectsPlayer(viewModelScope)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val allRooms: StateFlow<List<ChatRoomEntity>> = repository.getAllRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentRoom: StateFlow<ChatRoomEntity?> = _uiState.flatMapLatest { state ->
        repository.getRoom(state.currentRoomId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentMessages: StateFlow<List<ChatMessageEntity>> = _uiState.flatMapLatest { state ->
        repository.getMessages(state.currentRoomId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Social Posts (منشورات زي الفيس)
    private val _socialPosts = MutableStateFlow<List<SocialPost>>(emptyList())
    val socialPosts: StateFlow<List<SocialPost>> = _socialPosts.asStateFlow()

    // Community Groups (المجموعات)
    private val _communityGroups = MutableStateFlow<List<CommunityGroup>>(emptyList())
    val communityGroups: StateFlow<List<CommunityGroup>> = _communityGroups.asStateFlow()

    // Admin Support & Banned Accounts
    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _bannedAccounts = MutableStateFlow<List<BannedAccount>>(emptyList())
    val bannedAccounts: StateFlow<List<BannedAccount>> = _bannedAccounts.asStateFlow()

    // Friends & Direct Messaging
    private val _friendsList = MutableStateFlow<List<FriendUser>>(emptyList())
    val friendsList: StateFlow<List<FriendUser>> = _friendsList.asStateFlow()

    private val _friendRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val friendRequests: StateFlow<List<FriendRequest>> = _friendRequests.asStateFlow()

    private val _directMessages = MutableStateFlow<List<DirectMessage>>(emptyList())
    val directMessages: StateFlow<List<DirectMessage>> = _directMessages.asStateFlow()

    private val _isDirectFriendTyping = MutableStateFlow(false)
    val isDirectFriendTyping: StateFlow<Boolean> = _isDirectFriendTyping.asStateFlow()

    private val _userCoins = MutableStateFlow(1250)
    val userCoins: StateFlow<Int> = _userCoins.asStateFlow()

    private val _storiesList = MutableStateFlow<List<FriendStory>>(emptyList())
    val storiesList: StateFlow<List<FriendStory>> = _storiesList.asStateFlow()

    init {
        seedInitialSocialData()
        seedFriendsAndDirectData()
    }

    private fun seedInitialSocialData() {
        val now = System.currentTimeMillis()
        _socialPosts.value = listOf(
            SocialPost(
                id = "post_1",
                authorName = "معتز 👑",
                authorEmail = "mohamed21348446@gmail.com",
                authorAvatar = "👑",
                authorCountryFlag = "🇪🇬",
                authorRole = "OWNER_ADMIN",
                timestamp = now - 1000 * 60 * 15,
                content = "يا مرحباً بكل الأصدقاء في تطبيق Egyptoo الجديد! 🇪🇬🎉 تم تجهيز غرف عامة وخاصة لجميع الدول العربية، بثوث صوتية نقية، ألعاب وموسيقى، ومشاركة منشورات يومية زي ما تحبوا. نتمنى لكم وقتاً ممتعاً!",
                feeling = "متحمس وفخور 🚀",
                likesCount = 48,
                userReaction = "LOVE",
                comments = listOf(
                    PostComment("c1", "سارة المنصور 🎨", "🎨", "ألف مبروك يا معتز! التصميم والألوان مبهرة والبث الصوتي سريع جداً 👏"),
                    PostComment("c2", "عمر الفهد ⚡", "⚡", "أحلى تطبيق عربي! دخلت غرفة التحديات ولعبت مسابقة وتحديت الشباب 🔥")
                ),
                countryCode = "EG"
            ),
            SocialPost(
                id = "post_2",
                authorName = "عمر الفهد ⚡",
                authorAvatar = "⚡",
                authorCountryFlag = "🇸🇦",
                timestamp = now - 1000 * 60 * 60,
                content = "مين جاهز لمسابقة أسئلة وتحدي سرعة بديهة اليوم في غرفة التحديات؟ الجوائز والسمعة على المحك! 🎮🏆",
                feeling = "في حلبة التحديات ⚔️",
                likesCount = 27,
                userReaction = "LIKE",
                comments = listOf(
                    PostComment("c3", "خالد العتيبي 🚀", "🚀", "أنا جاهز يا عمر! افتح الغرفة وخلنا نبدأ جولة X-O فوراً.")
                ),
                countryCode = "SA"
            ),
            SocialPost(
                id = "post_3",
                authorName = "سارة المنصور 🎨",
                authorAvatar = "🎨",
                authorCountryFlag = "🇲🇦",
                timestamp = now - 1000 * 60 * 180,
                content = "أجمل سيمفونية استمعت لها اليوم عبر مشغل الغرفة الجماعي! الموسيقى متناسقة تماماً مع الحديث الصوتي 🎶🎧",
                feeling = "مستمتعة بالموسيقى 🎵",
                likesCount = 35,
                userReaction = "FIRE",
                comments = emptyList(),
                countryCode = "MA"
            )
        )

        _communityGroups.value = listOf(
            CommunityGroup(
                id = "grp_egypt_youth",
                name = "ملتقى شباب مصر 🇪🇬",
                description = "المجتمع الرسمي لشباب مصر - سوالف، فعاليات، ومسابقات أسبوعية",
                icon = "🇪🇬",
                countryCode = "EG",
                countryFlag = "🇪🇬",
                memberCount = 3840,
                isPrivate = false,
                isJoined = true,
                category = "شباب ومجتمع",
                recentActivity = "منشور جديد قبل 10 دقائق"
            ),
            CommunityGroup(
                id = "grp_gaming_arab",
                name = "نادي الجيمرز والتحديات 🎮",
                description = "عشاق الألعاب الإلكترونية، التحديات، مسابقات الذكاء والـ Gaming",
                icon = "🎮",
                countryCode = "ALL",
                countryFlag = "🌐",
                memberCount = 5620,
                isPrivate = false,
                isJoined = true,
                category = "ألعاب وترفيه",
                recentActivity = "تحدي مباشر نشط الآن"
            ),
            CommunityGroup(
                id = "grp_tech_ai",
                name = "رواد التقنية والبرمجة 💻",
                description = "مناقشات أحدث تقنيات الهواتف والذكاء الاصطناعي والتطوير",
                icon = "💻",
                countryCode = "ALL",
                countryFlag = "🌐",
                memberCount = 2100,
                isPrivate = false,
                isJoined = false,
                category = "تقنية ومعرفة",
                recentActivity = "مقال جديد عن أحدث التطبيقات"
            ),
            CommunityGroup(
                id = "grp_vip_private",
                name = "مجلس VIP الخاص 🔒👑",
                description = "مجموعة خاصة سرية بإشراف معتز تتطلب موافقة الإدارة للدخول",
                icon = "👑",
                countryCode = "EG",
                countryFlag = "🇪🇬",
                memberCount = 18,
                isPrivate = true,
                isJoined = true,
                category = "خاصة ومغلقة",
                recentActivity = "مناقشة إدارية"
            )
        )

        _supportTickets.value = listOf(
            SupportTicket(
                id = "ticket_101",
                userName = "خالد العتيبي",
                userEmail = "khalid_user@example.com",
                subject = "طلب توثيق الحساب بالشارة الذهبية 🌟",
                message = "السلام عليكم يا أستاذ معتز، أنا صانع محتوى في غرفة التحديات وأطلب توثيق حسابي في Egyptoo.",
                status = "PENDING"
            ),
            SupportTicket(
                id = "ticket_102",
                userName = "أحمد رضوان",
                userEmail = "ahmed_rad@example.com",
                subject = "اقتراح إضافة أعلام جديدة 🌍",
                message = "تطبيق رائع يا معتز، نقترح زيادة عدد أعلام الدول الإضافية للغرف العامة.",
                status = "RESOLVED",
                adminReply = "أهلاً بك! تم إضافة قائمة كاملة بالأعلام والدول العربية والعالمية. شكراً لدعمك يا أحمد!"
            )
        )

        _bannedAccounts.value = listOf(
            BannedAccount(
                email = "spammer_troll@example.com",
                userName = "حساب مخالف",
                reason = "إرسال رسائل إعلانية مزعجة في الغرف العامة"
            )
        )
    }

    private fun seedFriendsAndDirectData() {
        _friendsList.value = listOf(
            FriendUser(
                id = "friend_omar",
                name = "عمر الفهد",
                email = "omar_alfahad@gmail.com",
                avatar = "⚡",
                countryFlag = "🇸🇦",
                countryCode = "SA",
                isOnline = true,
                statusMessage = "جاهز لمسابقات وتحديات Egyptoo 🎮",
                vipBadge = "بطل التحديات 🏆",
                egyptooCoins = 820,
                gender = com.example.data.model.UserGender.MALE,
                age = 25,
                bio = "بطل التحديات ومحب لألعاب X-O والمنافسات في غرف Egyptoo 🏆"
            ),
            FriendUser(
                id = "friend_sara",
                name = "سارة المنصور",
                email = "sara_mansoor@facebook.com",
                avatar = "🎨",
                countryFlag = "🇲🇦",
                countryCode = "MA",
                isOnline = true,
                statusMessage = "استمع للموسيقى الهادئة في الروم 🎶",
                vipBadge = "فنانة المجتمع 🎨",
                egyptooCoins = 640,
                gender = com.example.data.model.UserGender.FEMALE,
                age = 22,
                bio = "فنانة تشكيلية ورسامة، أحب السوالف والموسيقى الهادئة 🎨✨"
            ),
            FriendUser(
                id = "friend_khalid",
                name = "خالد العتيبي",
                email = "khalid@gmail.com",
                avatar = "🚀",
                countryFlag = "🇰🇼",
                countryCode = "KW",
                isOnline = false,
                statusMessage = "في بث صوتي خاص...",
                vipBadge = "صانع محتوى 🌟",
                egyptooCoins = 490,
                gender = com.example.data.model.UserGender.MALE,
                age = 27,
                bio = "صانع محتوى تقني وبثوث صوتية حية في الغرف العربية 🚀"
            ),
            FriendUser(
                id = "friend_layla",
                name = "ليلى الهاشمي",
                email = "layla@gmail.com",
                avatar = "🌸",
                countryFlag = "🇦🇪",
                countryCode = "AE",
                isOnline = true,
                statusMessage = "أهلاً بالجميع في Egyptoo ✨",
                vipBadge = "عضو ماسي 💎",
                egyptooCoins = 950,
                gender = com.example.data.model.UserGender.FEMALE,
                age = 23,
                bio = "عضوة مميزة من دبي، أرحب بكل الأصدقاء في شات ومجموعات Egyptoo 🌸"
            )
        )

        _friendRequests.value = listOf(
            FriendRequest(
                id = "req_1",
                fromUser = FriendUser(
                    id = "friend_ahmed",
                    name = "أحمد رضوان",
                    email = "ahmed_rad@example.com",
                    avatar = "🌟",
                    countryFlag = "🇪🇬",
                    statusMessage = "محتاج أتواصل معك بخصوص الغرف",
                    gender = com.example.data.model.UserGender.MALE,
                    age = 26,
                    bio = "مطور ومحب للبرمجة والمجتمعات التقنية 💻"
                )
            )
        )

        _directMessages.value = listOf(
            DirectMessage(
                id = "dm_1",
                senderId = "friend_omar",
                receiverId = "me",
                senderName = "عمر الفهد",
                senderAvatar = "⚡",
                content = "يا هلا يا معتز! الغرفة شغالة عندك تمام؟ التشفير سريع جداً والرسائل واضحة 🔒"
            ),
            DirectMessage(
                id = "dm_2",
                senderId = "me",
                receiverId = "friend_omar",
                senderName = "معتز 👑",
                senderAvatar = "👑",
                content = "أهلاً يا عمر، كله تمام والاتصال الصوتي مشفر 256-bit بالكامل 🚀"
            )
        )

        _storiesList.value = listOf(
            FriendStory(
                id = "st_1",
                authorName = "معتز 👑",
                authorAvatar = "👑",
                authorFlag = "🇪🇬",
                mediaEmoji = "🎉",
                caption = "أهلاً بكم في إطلاق Egyptoo الرسمي! غرف مميزة وخصائص جديدة."
            ),
            FriendStory(
                id = "st_2",
                authorName = "عمر الفهد",
                authorAvatar = "⚡",
                authorFlag = "🇸🇦",
                mediaEmoji = "🎮",
                caption = "فوز ساحق في جولة X-O اليوم!"
            ),
            FriendStory(
                id = "st_3",
                authorName = "سارة المنصور",
                authorAvatar = "🎨",
                authorFlag = "🇲🇦",
                mediaEmoji = "🎶",
                caption = "أجواء طربية مع أروع أغاني النيون."
            )
        )
    }

    // Authentication Actions (Mandatory Google / Facebook)
    fun login(email: String, name: String, avatar: String, provider: AuthProvider) {
        val trimmedEmail = email.trim()
        val isAdmin = trimmedEmail.equals("mohamed21348446@gmail.com", ignoreCase = true)
        val finalName = if (isAdmin) "معتز" else name
        val finalAvatar = if (isAdmin) "👑" else avatar

        _uiState.value = _uiState.value.copy(
            isAuthenticated = true,
            authProvider = provider,
            userEmail = trimmedEmail,
            userName = finalName,
            userAvatar = finalAvatar
        )
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            selectedDirectFriend = null,
            isInsideRoom = false
        )
    }

    fun isSuperAdmin(): Boolean {
        return _uiState.value.userEmail.trim().equals("mohamed21348446@gmail.com", ignoreCase = true)
    }

    // Navigation Actions
    fun selectNavTab(tab: EgyptooNavTab) {
        _uiState.value = _uiState.value.copy(
            selectedNavTab = tab,
            isInsideRoom = false,
            selectedDirectFriend = null
        )
    }

    fun selectCountry(country: CountryItem) {
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            showCountryLanguageDialog = false
        )
    }

    fun selectLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(
            selectedLanguage = lang,
            showCountryLanguageDialog = false
        )
    }

    fun setShowCountryLanguageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCountryLanguageDialog = show)
    }

    fun setRoomFilterTab(index: Int) {
        _uiState.value = _uiState.value.copy(roomFilterTab = index)
    }

    fun selectRoom(roomId: String) {
        val room = allRooms.value.firstOrNull { it.id == roomId }
        if (room != null && room.isPrivate && room.accessPin.isNotEmpty() && !isSuperAdmin()) {
            _uiState.value = _uiState.value.copy(
                privateRoomPinPromptId = roomId,
                privateRoomError = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            currentRoomId = roomId,
            isInsideRoom = true
        )

        if (isSuperAdmin() && !_uiState.value.isGhostModeActive) {
            triggerGrandEntrance(roomId)
        }
    }

    fun verifyAndEnterPrivateRoom(enteredPin: String) {
        val roomId = _uiState.value.privateRoomPinPromptId ?: return
        val room = allRooms.value.firstOrNull { it.id == roomId }
        if (room == null) {
            _uiState.value = _uiState.value.copy(privateRoomPinPromptId = null)
            return
        }

        if (enteredPin.trim() == room.accessPin.trim() || isSuperAdmin()) {
            _uiState.value = _uiState.value.copy(
                currentRoomId = roomId,
                isInsideRoom = true,
                privateRoomPinPromptId = null,
                privateRoomError = null
            )
            if (isSuperAdmin() && !_uiState.value.isGhostModeActive) {
                triggerGrandEntrance(roomId)
            }
        } else {
            _uiState.value = _uiState.value.copy(privateRoomError = "رمز PIN غير صحيح! تأكد من المشرف.")
        }
    }

    fun dismissPrivatePinPrompt() {
        _uiState.value = _uiState.value.copy(privateRoomPinPromptId = null, privateRoomError = null)
    }

    fun navigateBackToChatsList() {
        _uiState.value = _uiState.value.copy(isInsideRoom = false, selectedDirectFriend = null)
    }

    fun onInputTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun toggleEncryptedView() {
        _uiState.value = _uiState.value.copy(
            showEncryptedCiphertext = !_uiState.value.showEncryptedCiphertext
        )
    }

    fun toggleVoiceStageExpanded() {
        _uiState.value = _uiState.value.copy(
            isVoiceStageExpanded = !_uiState.value.isVoiceStageExpanded
        )
    }

    fun toggleMusicPlayerExpanded() {
        _uiState.value = _uiState.value.copy(
            isMusicPlayerExpanded = !_uiState.value.isMusicPlayerExpanded
        )
    }

    fun setShowSecurityDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSecurityDialog = show)
    }

    fun setShowGamesPicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showGamesPicker = show)
    }

    fun setShowMusicQueue(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMusicQueue = show)
    }

    fun setShowCreateRoomDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateRoomDialog = show)
    }

    fun createNewRoom(
        name: String,
        description: String,
        isPrivate: Boolean,
        accessPin: String,
        countryCode: String,
        countryFlag: String,
        category: String
    ) {
        if (name.isBlank()) return
        val newId = "room_${UUID.randomUUID().toString().take(8)}"
        val newRoom = ChatRoomEntity(
            id = newId,
            name = name,
            description = description,
            isGroup = true,
            isPrivate = isPrivate,
            accessPin = accessPin,
            countryCode = countryCode,
            countryFlag = countryFlag,
            memberCount = 1,
            lastMessage = "تم إنشاء الغرفة بواسطة معتز 👑",
            lastMessageTimestamp = System.currentTimeMillis(),
            isE2EEVerified = true,
            avatarInitials = countryFlag,
            category = category
        )
        viewModelScope.launch {
            repository.createRoom(newRoom)
            _uiState.value = _uiState.value.copy(showCreateRoomDialog = false)
            selectRoom(newId)
        }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return

        val roomId = _uiState.value.currentRoomId
        _uiState.value = _uiState.value.copy(inputText = "")

        val isMoataz = isSuperAdmin()
        val formattedText = when {
            _uiState.value.isCatLanguagePrankActive -> "$text (مياووو كياووو 🐱🐾)"
            _uiState.value.isHeliumAlienVoiceActive -> "~🎈 صوت هيليوم: $text 👽~"
            _uiState.value.isMirrorReverseActive -> text.reversed()
            else -> text
        }

        val senderDisplayName = if (isMoataz) "${_uiState.value.userName} 👑" else _uiState.value.userName

        viewModelScope.launch {
            repository.sendMessage(
                roomId = roomId,
                content = formattedText,
                type = MessageType.TEXT,
                senderName = senderDisplayName,
                senderAvatar = _uiState.value.userAvatar,
                senderGender = _uiState.value.userGender.name,
                senderAge = _uiState.value.userAge,
                isOrnateRoyal = isMoataz
            )
            triggerSimulatedReply(roomId, text)
        }
    }

    fun sendVoiceNote() {
        val roomId = _uiState.value.currentRoomId
        val duration = Random.nextInt(3, 12)
        viewModelScope.launch {
            repository.sendMessage(
                roomId = roomId,
                content = "مقطع صوتي ($duration ث)",
                type = MessageType.VOICE_NOTE,
                audioDuration = duration
            )
            _uiState.value = _uiState.value.copy(isRecordingVoiceNote = false, recordedDurationSec = 0)

            delay(1500)
            repository.insertBotReply(
                roomId = roomId,
                senderName = "سارة المنصور 🎨",
                reply = "🎙️ صوتك نقي ومسموع في البث الصوتي لتطبيق Egyptoo يا معتز!"
            )
        }
    }

    fun shareCurrentTrackToChat() {
        val track = musicPlayer.playerState.value.currentTrack
        val roomId = _uiState.value.currentRoomId
        viewModelScope.launch {
            repository.sendMessage(
                roomId = roomId,
                content = "🎵 استمعوا معاً في Egyptoo: ${track.title} - ${track.artist}",
                type = MessageType.MUSIC_TRACK,
                gamePayload = "${track.title} - ${track.artist}"
            )
            repository.setActiveMusicTrack(roomId, track.title)
        }
    }

    fun launchGameFromPicker(gameType: GameType) {
        setShowGamesPicker(false)
        gamesManager.launchGame(gameType)

        val roomId = _uiState.value.currentRoomId
        val desc = when (gameType) {
            GameType.TRIVIA -> "بدأ تحدي مسابقة الذكاء والسرعة! أجب على الأسئلة قبل انتهاء العداد 🧠"
            GameType.SPIN_WHEEL -> "دوران عجلة التحديات والصراحة بدأ! مين جاهز للتحدي؟ 🎡"
            GameType.TIC_TAC_TOE -> "بدأت جولة X-O حماسية في الغرفة! دورك باللعب ⚔️"
            GameType.NONE -> ""
        }

        viewModelScope.launch {
            repository.sendMessage(
                roomId = roomId,
                content = "🎮 تحدي جماعي: ${gameType.titleAr}\n$desc",
                type = MessageType.GAME_INVITE,
                gamePayload = gameType.name
            )
        }
    }

    fun postGameScoreToChat(resultText: String) {
        val roomId = _uiState.value.currentRoomId
        viewModelScope.launch {
            repository.sendMessage(
                roomId = roomId,
                content = resultText,
                type = MessageType.GAME_RESULT
            )
        }
    }

    // Social Feed Actions
    fun createPost(content: String, feeling: String) {
        if (content.isBlank()) return
        val currentCountry = _uiState.value.selectedCountry
        val newPost = SocialPost(
            id = "post_${UUID.randomUUID().toString().take(6)}",
            authorName = "معتز 👑",
            authorEmail = _uiState.value.userEmail,
            authorAvatar = "👑",
            authorCountryFlag = currentCountry.flag,
            authorRole = "OWNER_ADMIN",
            timestamp = System.currentTimeMillis(),
            content = content,
            feeling = feeling,
            likesCount = 1,
            userReaction = "LOVE",
            countryCode = currentCountry.code
        )
        _socialPosts.value = listOf(newPost) + _socialPosts.value
    }

    fun togglePostReaction(postId: String, reaction: String) {
        _socialPosts.value = _socialPosts.value.map { post ->
            if (post.id == postId) {
                val isSame = post.userReaction == reaction
                val newReaction = if (isSame) null else reaction
                val countDiff = if (isSame) -1 else if (post.userReaction == null) 1 else 0
                post.copy(
                    userReaction = newReaction,
                    likesCount = maxOf(0, post.likesCount + countDiff)
                )
            } else post
        }
    }

    fun addPostComment(postId: String, commentText: String) {
        if (commentText.isBlank()) return
        val newComment = PostComment(
            id = "c_${UUID.randomUUID().toString().take(5)}",
            authorName = "معتز 👑",
            authorAvatar = "👑",
            content = commentText,
            timestamp = System.currentTimeMillis()
        )
        _socialPosts.value = _socialPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(comments = post.comments + newComment)
            } else post
        }
    }

    // Community Group Actions
    fun toggleJoinGroup(groupId: String) {
        _communityGroups.value = _communityGroups.value.map { group ->
            if (group.id == groupId) {
                val nowJoined = !group.isJoined
                group.copy(
                    isJoined = nowJoined,
                    memberCount = if (nowJoined) group.memberCount + 1 else group.memberCount - 1
                )
            } else group
        }
    }

    // Super Admin Actions & Pranks (Exclusive for mohamed21348446@gmail.com)
    fun setShowPrankArsenalDialog(show: Boolean) {
        if (!isSuperAdmin()) return
        _uiState.value = _uiState.value.copy(showPrankArsenalDialog = show)
    }

    fun toggleUpsideDownPrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isUpsideDownPrankActive
        _uiState.value = _uiState.value.copy(
            isUpsideDownPrankActive = !current,
            adminFeedbackMessage = if (!current) "تم تفعيل مقلب الجاذبية المعكوسة! الشاشة مقلوبة 180° 😂" else "تمت استعادة الجاذبية الطبيعية."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.CARTOON_BOOM)
        }
    }

    fun toggleDiscoPrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isDiscoPrankActive
        _uiState.value = _uiState.value.copy(
            isDiscoPrankActive = !current,
            adminFeedbackMessage = if (!current) "تم تشغيل حفلة الديسكو المجنونة! 🪩🔥" else "تم إيقاف الديسكو."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.ZAGHRUTA_HORN)
        }
    }

    fun toggleCatLanguagePrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isCatLanguagePrankActive
        _uiState.value = _uiState.value.copy(
            isCatLanguagePrankActive = !current,
            adminFeedbackMessage = if (!current) "تم تحويل لغة الشات إلى لغة القطط (مياوو كياوو) 🐱🐾" else "تمت استعادة اللغة العربية."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.QUACK_DUCK)
        }
    }

    fun toggleGhostMode() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isGhostModeActive
        _uiState.value = _uiState.value.copy(
            isGhostModeActive = !current,
            adminFeedbackMessage = if (!current) "تم تفعيل وضع الشبح والتخفي 👻 (أنت غير مرئي في الغرف)." else "تم إلغاء وضع الشبح."
        )
    }

    fun triggerFakeKickPrank() {
        if (!isSuperAdmin()) return
        _uiState.value = _uiState.value.copy(fakeKickPrankDialogActive = true)
        memeSoundsPlayer.playSound(MemeSoundType.ALIEN_SIREN)
    }

    fun dismissFakeKickPrank() {
        _uiState.value = _uiState.value.copy(fakeKickPrankDialogActive = false)
    }

    fun triggerPotatoMorph() {
        if (!isSuperAdmin()) return
        _friendsList.value = _friendsList.value.map {
            it.copy(avatar = "🥔", vipBadge = "بطاطس ملوكية 🥔")
        }
        _uiState.value = _uiState.value.copy(
            adminFeedbackMessage = "تم تحويل جميع الأعضاء في الغرفة إلى بطاطس مقرمشة 🥔🤣!"
        )
        memeSoundsPlayer.playSound(MemeSoundType.EVIL_LAUGH)
    }

    fun triggerGiftRain() {
        if (!isSuperAdmin()) return
        _uiState.value = _uiState.value.copy(
            giftRainCount = _uiState.value.giftRainCount + 1,
            adminFeedbackMessage = "تم إمطار الغرفة بمجوهرات وهدايا مجانية للجميع 💎🎁!"
        )
        addCoins(200)
        memeSoundsPlayer.playSound(MemeSoundType.ZAGHRUTA_HORN)
    }

    fun playMemeSound(type: MemeSoundType) {
        if (!isSuperAdmin()) return
        memeSoundsPlayer.playSound(type)
        _uiState.value = _uiState.value.copy(
            adminFeedbackMessage = "تم بث صوت: ${type.titleAr} في الغرفة فورياً 🔊"
        )
    }

    fun sendGlobalEmergencyBroadcast(text: String) {
        if (!isSuperAdmin() || text.isBlank()) return
        _uiState.value = _uiState.value.copy(
            globalBroadcastAlert = text,
            adminFeedbackMessage = "تم إرسال الإذاعة الملكية لجميع شاشات المستخدمين فورياً 📢"
        )
        memeSoundsPlayer.playSound(MemeSoundType.MEMO_HELLO)
    }

    fun dismissGlobalEmergencyBroadcast() {
        _uiState.value = _uiState.value.copy(globalBroadcastAlert = null)
    }

    // Grand Majestic Entrance (الدخول المهيب)
    fun triggerGrandEntrance(targetRoomId: String? = null) {
        val rId = targetRoomId ?: _uiState.value.currentRoomId
        _uiState.value = _uiState.value.copy(
            isGrandEntranceActive = true,
            grandEntranceTitle = "👑 ⚜️ دخول مهيب وفخامة ملكية لحضرة المشرف معتز ⚜️ 👑"
        )
        memeSoundsPlayer.playSound(MemeSoundType.ROYAL_FANFARE)
        viewModelScope.launch {
            repository.sendGrandEntranceAnnouncement(
                roomId = rId,
                senderName = "${_uiState.value.userName} 👑",
                senderAvatar = _uiState.value.userAvatar,
                senderGender = _uiState.value.userGender.name,
                senderAge = _uiState.value.userAge
            )
            delay(5000)
            _uiState.value = _uiState.value.copy(isGrandEntranceActive = false)
        }
    }

    fun dismissGrandEntrance() {
        _uiState.value = _uiState.value.copy(isGrandEntranceActive = false)
    }

    // Royal Broadcast to ALL Rooms & Groups (إرسال رسائل لكل المجموعات والغرف)
    fun sendBroadcastToAllRoomsAndGroups(messageText: String) {
        if (!isSuperAdmin() || messageText.isBlank()) return
        val currentRooms = allRooms.value
        val formattedMsg = "👑 ⚜️ برقية ملكية عامة لجميع الغرف والمجموعات من المشرف معتز ⚜️ 👑\n$messageText"
        viewModelScope.launch {
            repository.sendBroadcastToAllRooms(
                rooms = currentRooms,
                content = formattedMsg,
                senderName = "${_uiState.value.userName} 👑",
                senderAvatar = _uiState.value.userAvatar,
                senderGender = _uiState.value.userGender.name,
                senderAge = _uiState.value.userAge
            )
            // Add to social feed as a royal post
            createPost("📢 [برقية إدارية عامة]: $messageText", "فخور ومتحمس 👑")
            _uiState.value = _uiState.value.copy(
                globalBroadcastAlert = messageText,
                adminFeedbackMessage = "تم إرسال البرقية بنجاح إلى جميع الغرف (${currentRooms.size} غرفة) والمجموعات دفعة واحدة! 🌐✨"
            )
            memeSoundsPlayer.playSound(MemeSoundType.ROYAL_FANFARE)
        }
    }

    // Prank Target Scope Toggle (الغرفة الحالية vs جميع الغرف والمجموعات)
    fun togglePrankTargetScope() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.prankTargetAllRooms
        _uiState.value = _uiState.value.copy(
            prankTargetAllRooms = !current,
            adminFeedbackMessage = if (!current) "تم ضبط نطاق المقالب: لجميع الغرف والمجموعات دفعة واحدة 🌐" else "تم ضبط نطاق المقالب: للغرفة الحالية فقط 📍"
        )
    }

    // New Pranks Implementation
    fun toggleEarthquakePrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isEarthquakeActive
        _uiState.value = _uiState.value.copy(
            isEarthquakeActive = !current,
            adminFeedbackMessage = if (!current) "تم تفعيل مقلب الزلزال والاهتزاز العنيف! 🌋📳" else "تم إيقاف الزلزال."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.EARTHQUAKE_RUMBLE)
        }
    }

    fun toggleTomatoSplatPrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isTomatoSplatActive
        _uiState.value = _uiState.value.copy(
            isTomatoSplatActive = !current,
            adminFeedbackMessage = if (!current) "تم إطلاق عاصفة الطماطم والبيض والسلايم على الشاشة 🍅🥚!" else "تم تنظيف الشاشة من الطماطم."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.TOMATO_SPLAT)
        }
    }

    fun triggerFakeBatteryPrank() {
        if (!isSuperAdmin()) return
        _uiState.value = _uiState.value.copy(fakeBatteryPrankActive = true)
        memeSoundsPlayer.playSound(MemeSoundType.POLICE_SIREN)
    }

    fun dismissFakeBatteryPrank() {
        _uiState.value = _uiState.value.copy(fakeBatteryPrankActive = false)
    }

    fun toggleMatrixHackerPrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isMatrixHackerActive
        _uiState.value = _uiState.value.copy(
            isMatrixHackerActive = !current,
            adminFeedbackMessage = if (!current) "تم اختراق الشاشة بمصفوفة الهكر الخضراء 👨‍💻💚!" else "تم إغلاق شفرة المصفوفة."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.CARTOON_BOOM)
        }
    }

    fun toggleHeliumAlienPrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isHeliumAlienVoiceActive
        _uiState.value = _uiState.value.copy(
            isHeliumAlienVoiceActive = !current,
            adminFeedbackMessage = if (!current) "تم تفعيل فلتر صوت الهيليوم والفضائي 🎈👽!" else "تم تعطيل فلتر الهيليوم."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.ALIEN_SIREN)
        }
    }

    fun toggleSlowMoTurtlePrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isSlowMoTurtleActive
        _uiState.value = _uiState.value.copy(
            isSlowMoTurtleActive = !current,
            adminFeedbackMessage = if (!current) "تم تفعيل وضع السلحفاة والحركة البطيئة 🐢⏳!" else "تم إلغاء الحركة البطيئة."
        )
        if (!current) {
            memeSoundsPlayer.playSound(MemeSoundType.CRICKET_CHIRP)
        }
    }

    fun toggleHeliumVoicePrank() {
        toggleHeliumAlienPrank()
        _uiState.value = _uiState.value.copy(isHeliumVoiceActive = _uiState.value.isHeliumAlienVoiceActive)
    }

    fun toggleSlowMoPrank() {
        toggleSlowMoTurtlePrank()
        _uiState.value = _uiState.value.copy(isSlowMoActive = _uiState.value.isSlowMoTurtleActive)
    }

    fun openDirectChatWithUser(user: FriendUser) {
        _uiState.value = _uiState.value.copy(
            selectedDirectFriend = user,
            selectedNavTab = EgyptooNavTab.FRIENDS_AND_DMS,
            isInsideRoom = false
        )
    }

    fun toggleMirrorReversePrank() {
        if (!isSuperAdmin()) return
        val current = _uiState.value.isMirrorReverseActive
        _uiState.value = _uiState.value.copy(
            isMirrorReverseActive = !current,
            adminFeedbackMessage = if (!current) "تم عكس كل كلمات ونصوص الشات بالمرآة المعكوسة 🪞!" else "تمت استعادة الكتابة المعتدلة."
        )
    }

    // Profile Management Actions for Everyone (تعديل وتصفح الملفات الشخصية)
    fun setShowEditProfileDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showEditProfileDialog = show)
    }

    fun updateUserProfile(
        name: String,
        avatar: String,
        gender: com.example.data.model.UserGender,
        age: Int,
        bio: String
    ) {
        val trimmedName = name.trim().ifEmpty { _uiState.value.userName }
        val finalAvatar = avatar.trim().ifEmpty { _uiState.value.userAvatar }
        val finalAge = age.coerceIn(12, 99)
        _uiState.value = _uiState.value.copy(
            userName = trimmedName,
            userAvatar = finalAvatar,
            userGender = gender,
            userAge = finalAge,
            userBio = bio,
            showEditProfileDialog = false,
            adminFeedbackMessage = "تم حفظ وتحديث ملفك الشخصي بنجاح 🌟"
        )
    }

    fun inspectUserProfile(user: FriendUser) {
        _uiState.value = _uiState.value.copy(inspectedUserProfile = user)
    }

    fun inspectUserFromMessage(
        name: String,
        avatar: String,
        genderStr: String,
        age: Int
    ) {
        val isSenderAdmin = name.contains("معتز") || name.contains("👑")
        val gender = com.example.data.model.UserGender.fromCode(genderStr)
        val user = FriendUser(
            id = "user_${name.hashCode()}",
            name = name,
            email = if (isSenderAdmin) "mohamed21348446@gmail.com" else "user_${name.hashCode()}@egyptoo.app",
            avatar = avatar.ifEmpty { if (isSenderAdmin) "👑" else if (gender == com.example.data.model.UserGender.MALE) "👦" else "👧" },
            countryFlag = if (isSenderAdmin) "🇪🇬" else "🌐",
            statusMessage = if (isSenderAdmin) "المشرف العام ومؤسس مجتمع Egyptoo 👑" else "عضو مميز في الغرف الصوتية ✨",
            vipBadge = if (isSenderAdmin) "المالك والمشرف العام 👑" else "عضو نشط ⭐",
            egyptooCoins = if (isSenderAdmin) 99999 else 650,
            gender = gender,
            age = age,
            bio = if (isSenderAdmin) "المشرف العام وصاحب كافة الصلاحيات الملكية والمقالب في Egyptoo 👑⚜️" else "أحب الدردشة والتواصل الصوتي مع الأصدقاء في Egyptoo ✨",
            isSuperAdmin = isSenderAdmin
        )
        _uiState.value = _uiState.value.copy(inspectedUserProfile = user)
    }

    fun dismissInspectedUserProfile() {
        _uiState.value = _uiState.value.copy(inspectedUserProfile = null)
    }

    fun adminFreezeRoom(roomId: String, currentFrozen: Boolean) {
        if (!isSuperAdmin()) return
        viewModelScope.launch {
            repository.freezeRoom(roomId, !currentFrozen)
            _uiState.value = _uiState.value.copy(
                adminFeedbackMessage = if (!currentFrozen) "تم تجميد الغرفة وإيقاف الرسائل مؤقتاً." else "تم فك التجميد وتفعيل الغرفة."
            )
        }
    }

    fun adminDeleteRoom(roomId: String) {
        if (!isSuperAdmin()) return
        viewModelScope.launch {
            repository.deleteRoom(roomId)
            _uiState.value = _uiState.value.copy(adminFeedbackMessage = "تم حذف الغرفة بنجاح من النظام.")
        }
    }

    fun adminBanAccount(email: String, userName: String, reason: String) {
        if (!isSuperAdmin() || email.isBlank()) return
        val newBan = BannedAccount(
            email = email,
            userName = userName.ifEmpty { "مستخدم" },
            reason = reason.ifEmpty { "مخالفة معايير الإشراف في Egyptoo" },
            bannedAt = System.currentTimeMillis()
        )
        _bannedAccounts.value = listOf(newBan) + _bannedAccounts.value.filter { it.email != email }
        _uiState.value = _uiState.value.copy(adminFeedbackMessage = "تم حظر الحساب $email فورياً.")
    }

    fun adminUnbanAccount(email: String) {
        if (!isSuperAdmin()) return
        _bannedAccounts.value = _bannedAccounts.value.filter { it.email != email }
        _uiState.value = _uiState.value.copy(adminFeedbackMessage = "تم رفع الحظر عن $email.")
    }

    fun adminReplySupportTicket(ticketId: String, replyText: String) {
        if (!isSuperAdmin() || replyText.isBlank()) return
        _supportTickets.value = _supportTickets.value.map { ticket ->
            if (ticket.id == ticketId) {
                ticket.copy(status = "RESOLVED", adminReply = replyText)
            } else ticket
        }
        _uiState.value = _uiState.value.copy(adminFeedbackMessage = "تم الرد على الشكوى وحلها.")
    }

    fun clearAdminFeedbackMessage() {
        _uiState.value = _uiState.value.copy(adminFeedbackMessage = null)
    }

    // Friends & Direct Chat Management
    fun openDirectChat(friend: FriendUser) {
        _uiState.value = _uiState.value.copy(selectedDirectFriend = friend)
    }

    fun closeDirectChat() {
        _uiState.value = _uiState.value.copy(selectedDirectFriend = null)
    }

    fun sendDirectMessage(receiverId: String, content: String) {
        if (content.isBlank()) return
        val newMsg = DirectMessage(
            id = "dm_${UUID.randomUUID().toString().take(6)}",
            senderId = "me",
            receiverId = receiverId,
            senderName = _uiState.value.userName,
            senderAvatar = _uiState.value.userAvatar,
            content = content
        )
        _directMessages.value = _directMessages.value + newMsg

        // Trigger realistic reply from friend
        viewModelScope.launch {
            delay(1000)
            _isDirectFriendTyping.value = true
            delay(2000)
            _isDirectFriendTyping.value = false

            val friendObj = _friendsList.value.firstOrNull { it.id == receiverId }
            val replies = listOf(
                "رسالتك وصلت ومحفوظة بتشفير تام يا معتز 🔒👍",
                "أنا موجود في تطبيق Egyptoo، كلمني أي وقت صوت أو شات!",
                "تمام يا غالي، شفت التحديثات والخصائص الجديدة ممتازة جداً 🔥",
                "عجلة الحظ لفتها اليوم وربحت 250 عملة 🎡!"
            )
            val replyMsg = DirectMessage(
                id = "dm_${UUID.randomUUID().toString().take(6)}",
                senderId = receiverId,
                receiverId = "me",
                senderName = friendObj?.name ?: "الصديق",
                senderAvatar = friendObj?.avatar ?: "⚡",
                content = replies.random()
            )
            _directMessages.value = _directMessages.value + replyMsg
        }
    }

    fun sendDirectVoiceNote(receiverId: String) {
        val duration = Random.nextInt(4, 15)
        val newMsg = DirectMessage(
            id = "dm_${UUID.randomUUID().toString().take(6)}",
            senderId = "me",
            receiverId = receiverId,
            senderName = _uiState.value.userName,
            senderAvatar = _uiState.value.userAvatar,
            content = "مقطع صوتي خاص ($duration ث)",
            type = MessageType.VOICE_NOTE,
            audioDuration = duration
        )
        _directMessages.value = _directMessages.value + newMsg
    }

    fun sendDirectMediaEmoji(receiverId: String, emoji: String) {
        val newMsg = DirectMessage(
            id = "dm_${UUID.randomUUID().toString().take(6)}",
            senderId = "me",
            receiverId = receiverId,
            senderName = _uiState.value.userName,
            senderAvatar = _uiState.value.userAvatar,
            content = emoji,
            mediaEmoji = emoji
        )
        _directMessages.value = _directMessages.value + newMsg
    }

    fun acceptFriendRequest(reqId: String) {
        val req = _friendRequests.value.firstOrNull { it.id == reqId } ?: return
        _friendsList.value = _friendsList.value + req.fromUser
        _friendRequests.value = _friendRequests.value.filter { it.id != reqId }
        _uiState.value = _uiState.value.copy(
            adminFeedbackMessage = "تم قبول طلب الصداقة من ${req.fromUser.name} وأصبح بإمكانك مراسلته سرياً."
        )
    }

    fun declineFriendRequest(reqId: String) {
        _friendRequests.value = _friendRequests.value.filter { it.id != reqId }
    }

    fun sendFriendRequest(query: String) {
        val newReq = FriendRequest(
            id = "req_${UUID.randomUUID().toString().take(4)}",
            fromUser = FriendUser(
                id = "user_${UUID.randomUUID().toString().take(4)}",
                name = query.ifEmpty { "صديق جديد" },
                email = if (query.contains("@")) query else "$query@example.com",
                avatar = "⭐",
                countryFlag = _uiState.value.selectedCountry.flag,
                statusMessage = "طلب صداقة للتواصل الخاص في Egyptoo"
            )
        )
        _friendRequests.value = listOf(newReq) + _friendRequests.value
    }

    fun addCoins(amount: Int) {
        _userCoins.value = _userCoins.value + amount
    }

    private fun triggerSimulatedReply(roomId: String, userText: String) {
        viewModelScope.launch {
            delay(800)
            _uiState.value = _uiState.value.copy(activeFriendTyping = "عمر الفهد")
            delay(1800)
            _uiState.value = _uiState.value.copy(activeFriendTyping = null)

            val reply = when {
                userText.contains("أغني", true) || userText.contains("موسيقى", true) ->
                    "🎶 مشغل Egyptoo شغال، شغلت لك لحن هادئ بالأعلى! تقدر تضيف أغانيك للقائمة المشتركة."
                userText.contains("تحدي", true) || userText.contains("لعب", true) ->
                    "أنا جاهز للتحدي يا معتز! اضغط على زر الألعاب 🎮 وخلينا نلعب مسابقة أو جولة X-O."
                userText.contains("بث", true) || userText.contains("صوت", true) || userText.contains("مايك", true) ->
                    "🎙️ البث الصوتي المباشر واضح ونقي! صوتك واصل لجميع المتواجدين على المسرح."
                userText.contains("إشراف", true) || userText.contains("أدمن", true) ->
                    "👑 حسابك mohamed21348446@gmail.com هو صاحب لوحة الإشراف العليا وحظر الحسابات وإدارة المجموعات بالكامل."
                else -> {
                    val quickReplies = listOf(
                        "أهلاً بالمشرف معتز! البث الصوتي مشعلل في غرف Egyptoo 🔥",
                        "تطبيق Egyptoo طالع فخم ومسلي جداً، منشورات الفيس والغرف شغالة تمام!",
                        "التشفير التام 256-bit مفعل بيننا، كل المحادثات آمنة تماماً 🔒"
                    )
                    quickReplies.random()
                }
            }
            val finalReply = if (_uiState.value.isCatLanguagePrankActive) {
                "$reply (مياووو 🐱🐾)"
            } else reply

            repository.insertBotReply(roomId, "عمر الفهد ⚡", finalReply)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.release()
        voiceBroadcast.release()
    }
}
