package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

data class StageParticipant(
    val id: String,
    val name: String,
    val role: String, // "مقدم البث" (Host), "متحدث" (Speaker), "مستمع" (Listener)
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val hasRaisedHand: Boolean = false,
    val avatarInitials: String = "👤",
    val audioLevel: Float = 0f
)

data class FloatingReaction(
    val id: Long,
    val emoji: String,
    val startXRatio: Float, // 0.1 to 0.9
    val scale: Float = 1f
)

data class LiveVoiceState(
    val isLiveActive: Boolean = true,
    val stageTitle: String = "🎙️ مسرح النقاش والموسيقى المباشر",
    val isMyMicMuted: Boolean = false,
    val amISpeaker: Boolean = true,
    val didIRaiseHand: Boolean = false,
    val speakers: List<StageParticipant> = emptyList(),
    val listeners: List<StageParticipant> = emptyList(),
    val floatingReactions: List<FloatingReaction> = emptyList(),
    val myMicAmplitude: Float = 0f
)

class LiveVoiceBroadcastManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var recordingJob: Job? = null
    private var simulationJob: Job? = null
    private var audioRecord: AudioRecord? = null

    private val initialSpeakers = listOf(
        StageParticipant(
            id = "user_me",
            name = "أنا (المضيف)",
            role = "مقدم البث",
            isSpeaking = false,
            isMuted = false,
            avatarInitials = "👑"
        ),
        StageParticipant(
            id = "speaker_sarah",
            name = "سارة المنصور",
            role = "متحدث",
            isSpeaking = true,
            isMuted = false,
            avatarInitials = "س"
        ),
        StageParticipant(
            id = "speaker_omar",
            name = "عمر الفهد",
            role = "متحدث",
            isSpeaking = false,
            isMuted = false,
            avatarInitials = "ع"
        )
    )

    private val initialListeners = listOf(
        StageParticipant(
            id = "listener_khalid",
            name = "خالد العتيبي",
            role = "مستمع",
            avatarInitials = "خ"
        ),
        StageParticipant(
            id = "listener_reem",
            name = "ريم الشمري",
            role = "مستمع",
            avatarInitials = "ر"
        ),
        StageParticipant(
            id = "listener_fahad",
            name = "فهد الدوسري",
            role = "مستمع",
            avatarInitials = "ف"
        )
    )

    private val _voiceState = MutableStateFlow(
        LiveVoiceState(
            isLiveActive = true,
            speakers = initialSpeakers,
            listeners = initialListeners
        )
    )
    val voiceState: StateFlow<LiveVoiceState> = _voiceState.asStateFlow()

    init {
        startSimulation()
    }

    fun toggleMicMute() {
        val newMuted = !_voiceState.value.isMyMicMuted
        _voiceState.value = _voiceState.value.copy(
            isMyMicMuted = newMuted,
            speakers = _voiceState.value.speakers.map {
                if (it.id == "user_me") it.copy(isMuted = newMuted, isSpeaking = !newMuted && it.audioLevel > 0.1f)
                else it
            }
        )
        if (newMuted) {
            stopMicRecording()
        } else {
            startMicRecordingIfPermitted()
        }
    }

    fun toggleRaiseHand() {
        val newRaised = !_voiceState.value.didIRaiseHand
        _voiceState.value = _voiceState.value.copy(didIRaiseHand = newRaised)
    }

    fun toggleLiveBroadcast() {
        val newState = !_voiceState.value.isLiveActive
        _voiceState.value = _voiceState.value.copy(isLiveActive = newState)
        if (newState) {
            startSimulation()
            startMicRecordingIfPermitted()
        } else {
            stopMicRecording()
            simulationJob?.cancel()
        }
    }

    fun sendReaction(emoji: String) {
        val newReaction = FloatingReaction(
            id = System.currentTimeMillis() + Random.nextLong(1000),
            emoji = emoji,
            startXRatio = Random.nextFloat() * 0.7f + 0.15f,
            scale = Random.nextFloat() * 0.4f + 0.8f
        )
        val current = _voiceState.value.floatingReactions
        _voiceState.value = _voiceState.value.copy(
            floatingReactions = (current + newReaction).takeLast(8)
        )

        // Clear reaction after short delay
        scope.launch {
            delay(2800)
            _voiceState.value = _voiceState.value.copy(
                floatingReactions = _voiceState.value.floatingReactions.filter { it.id != newReaction.id }
            )
        }
    }

    fun startMicRecordingIfPermitted() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission || _voiceState.value.isMyMicMuted || !_voiceState.value.isLiveActive) {
            return
        }

        stopMicRecording()
        recordingJob = scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 16000
                val minBuf = AudioRecord.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                @SuppressLint("MissingPermission")
                val record = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBuf.coerceAtLeast(2048)
                )
                audioRecord = record
                record.startRecording()

                val buffer = ShortArray(1024)
                while (isActive && !_voiceState.value.isMyMicMuted) {
                    val read = record.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        var sum = 0L
                        for (i in 0 until read) {
                            sum += abs(buffer[i].toInt())
                        }
                        val avg = (sum / read).toFloat()
                        val normalized = (avg / 3000f).coerceIn(0f, 1f)
                        val isTalking = normalized > 0.08f

                        _voiceState.value = _voiceState.value.copy(
                            myMicAmplitude = normalized,
                            speakers = _voiceState.value.speakers.map {
                                if (it.id == "user_me") it.copy(
                                    isSpeaking = isTalking,
                                    audioLevel = normalized
                                ) else it
                            }
                        )
                    }
                    delay(80)
                }
            } catch (_: Exception) {
                // AudioRecord error or permission denial handled gracefully
            }
        }
    }

    private fun stopMicRecording() {
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = scope.launch(Dispatchers.Default) {
            var tick = 0
            while (isActive) {
                delay(1200)
                tick++

                // Toggle other speakers speaking status realistically
                val sarahSpeaking = (tick % 3 == 0 || tick % 4 == 0)
                val omarSpeaking = !sarahSpeaking && (tick % 2 == 0)

                val updatedSpeakers = _voiceState.value.speakers.map { sp ->
                    when (sp.id) {
                        "speaker_sarah" -> sp.copy(
                            isSpeaking = sarahSpeaking,
                            audioLevel = if (sarahSpeaking) Random.nextFloat() * 0.6f + 0.3f else 0f
                        )
                        "speaker_omar" -> sp.copy(
                            isSpeaking = omarSpeaking,
                            audioLevel = if (omarSpeaking) Random.nextFloat() * 0.7f + 0.2f else 0f
                        )
                        else -> sp
                    }
                }

                _voiceState.value = _voiceState.value.copy(speakers = updatedSpeakers)

                // Occasionally drop an ambient reaction from friends
                if (tick % 7 == 0 && _voiceState.value.isLiveActive) {
                    val emojis = listOf("❤️", "🔥", "👏", "🎵", "✨")
                    sendReaction(emojis[tick % emojis.size])
                }
            }
        }
    }

    fun release() {
        stopMicRecording()
        simulationJob?.cancel()
    }
}
