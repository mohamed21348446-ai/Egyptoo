package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class TrackItem(
    val id: String,
    val title: String,
    val artist: String,
    val durationSec: Int,
    val genre: String,
    val baseFrequencies: List<Double>,
    val addedBy: String = "عمر الفهد",
    val votes: Int = 1
)

data class MusicPlayerState(
    val currentTrack: TrackItem,
    val isPlaying: Boolean = false,
    val currentPositionSec: Float = 0f,
    val visualizerAmplitudes: List<Float> = List(16) { 0.2f },
    val activeListeners: Int = 4,
    val playlist: List<TrackItem> = emptyList()
)

class MusicSynthPlayer(private val scope: CoroutineScope) {

    private val sampleRate = 22050
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null

    private val defaultTracks = listOf(
        TrackItem(
            id = "track_1",
            title = "ليالي النيون (Neon Nights Chill)",
            artist = "Atheer Beats",
            durationSec = 180,
            genre = "Lo-Fi Lounge",
            baseFrequencies = listOf(261.63, 329.63, 392.00, 493.88), // Cmaj7
            addedBy = "عمر الفهد",
            votes = 5
        ),
        TrackItem(
            id = "track_2",
            title = "أثير الأندلس (Andalusian Ambient)",
            artist = "Sultans of Sound",
            durationSec = 210,
            genre = "Acoustic Oriental",
            baseFrequencies = listOf(220.00, 261.63, 329.63, 440.00), // Am
            addedBy = "سارة المنصور",
            votes = 4
        ),
        TrackItem(
            id = "track_3",
            title = "حماس التحدي (Synthwave Arena)",
            artist = "Cyber Pulse",
            durationSec = 160,
            genre = "Retro Wave",
            baseFrequencies = listOf(146.83, 220.00, 293.66, 370.00), // Dm
            addedBy = "خالد العتيبي",
            votes = 3
        ),
        TrackItem(
            id = "track_4",
            title = "هدوء السكون (Midnight Rain)",
            artist = "Lofi Clouds",
            durationSec = 240,
            genre = "Lo-Fi Beats",
            baseFrequencies = listOf(174.61, 220.00, 261.63, 349.23), // Fmaj7
            addedBy = "أنا",
            votes = 2
        )
    )

    private val _playerState = MutableStateFlow(
        MusicPlayerState(
            currentTrack = defaultTracks[0],
            isPlaying = false,
            playlist = defaultTracks
        )
    )
    val playerState: StateFlow<MusicPlayerState> = _playerState.asStateFlow()

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (_playerState.value.isPlaying) return

        initAudioTrack()
        _playerState.value = _playerState.value.copy(isPlaying = true)

        playbackJob?.cancel()
        playbackJob = scope.launch(Dispatchers.Default) {
            val track = _playerState.value.currentTrack
            var step = 0
            val bufferSize = 2048
            val shortBuffer = ShortArray(bufferSize)

            while (isActive && _playerState.value.isPlaying) {
                // Generate soft harmonized musical tones
                val freqIndex = (step / 8) % track.baseFrequencies.size
                val currentFreq = track.baseFrequencies[freqIndex]
                val subBass = currentFreq / 2.0

                for (i in 0 until bufferSize) {
                    val time = (step * bufferSize + i).toDouble() / sampleRate
                    // Harmonic chord synthesis
                    val wave1 = sin(2.0 * Math.PI * currentFreq * time)
                    val wave2 = sin(2.0 * Math.PI * (currentFreq * 1.5) * time) * 0.4
                    val bass = sin(2.0 * Math.PI * subBass * time) * 0.5
                    
                    // Soft envelope to avoid clicks
                    val envelope = (0.5 + 0.5 * sin(time * 3.0)).toFloat()
                    val sample = ((wave1 + wave2 + bass) * 0.25 * envelope * Short.MAX_VALUE).toInt()
                    shortBuffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                try {
                    audioTrack?.write(shortBuffer, 0, bufferSize)
                } catch (_: Exception) {}

                step++
                
                // Update progress & visualizer bars
                val newPos = (_playerState.value.currentPositionSec + 0.1f) % track.durationSec
                val randomVisualizer = List(16) { 
                    (0.15f + 0.75f * (sin(step * 0.3 + it).toFloat() * 0.5f + 0.5f)).coerceIn(0.1f, 1f)
                }
                
                _playerState.value = _playerState.value.copy(
                    currentPositionSec = newPos,
                    visualizerAmplitudes = randomVisualizer
                )

                delay(70)
            }
        }
    }

    fun pause() {
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            visualizerAmplitudes = List(16) { 0.1f }
        )
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (_: Exception) {}
    }

    fun nextTrack() {
        val current = _playerState.value.currentTrack
        val list = _playerState.value.playlist
        val nextIndex = (list.indexOf(current) + 1) % list.size
        selectTrack(list[nextIndex])
    }

    fun prevTrack() {
        val current = _playerState.value.currentTrack
        val list = _playerState.value.playlist
        val prevIndex = if (list.indexOf(current) - 1 < 0) list.size - 1 else list.indexOf(current) - 1
        selectTrack(list[prevIndex])
    }

    fun selectTrack(track: TrackItem) {
        val wasPlaying = _playerState.value.isPlaying
        pause()
        _playerState.value = _playerState.value.copy(
            currentTrack = track,
            currentPositionSec = 0f
        )
        if (wasPlaying) {
            play()
        }
    }

    fun addTrackToQueue(title: String, artist: String, genre: String = "طلب الأصدقاء") {
        val newTrack = TrackItem(
            id = "custom_${System.currentTimeMillis()}",
            title = title,
            artist = artist,
            durationSec = 200,
            genre = genre,
            baseFrequencies = listOf(293.66, 369.99, 440.00, 554.37), // Dmaj7
            addedBy = "أنا",
            votes = 1
        )
        val updatedList = _playerState.value.playlist + newTrack
        _playerState.value = _playerState.value.copy(playlist = updatedList)
    }

    fun upvoteTrack(trackId: String) {
        val updated = _playerState.value.playlist.map {
            if (it.id == trackId) it.copy(votes = it.votes + 1) else it
        }.sortedByDescending { it.votes }
        _playerState.value = _playerState.value.copy(playlist = updated)
    }

    fun release() {
        pause()
        try {
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    private fun initAudioTrack() {
        if (audioTrack == null) {
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf.coerceAtLeast(4096))
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack?.play()
        }
    }
}
