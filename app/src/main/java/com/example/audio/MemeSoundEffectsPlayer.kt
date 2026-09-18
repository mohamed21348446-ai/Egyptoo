package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class MemeSoundType(val titleAr: String, val emoji: String) {
    EVIL_LAUGH("ضحكة شريرة 😈", "😈"),
    QUACK_DUCK("بطة تكاكي 🦆", "🦆"),
    CARTOON_BOOM("انفجار ميمز 💥", "💥"),
    ALIEN_SIREN("صرخة فضائي 👽", "👽"),
    ZAGHRUTA_HORN("احتفال وزغروطة 🎺", "🎺"),
    MEMO_HELLO("ألو مين هناك 📞", "📞"),
    ROYAL_FANFARE("نفير ملكي مهيب 👑", "👑"),
    EARTHQUAKE_RUMBLE("زلزال واهتزاز عنيف 🌋", "🌋"),
    TOMATO_SPLAT("رمي طماطم وسلايم 🍅", "🍅"),
    CRICKET_CHIRP("صرصور الحقل الصامت 🦗", "🦗"),
    POLICE_SIREN("سارينة شرطة وإنذار 🚨", "🚨"),
    APPLAUSE_CHEER("تصفيق وتصفير جماعي 👏", "👏")
}

class MemeSoundEffectsPlayer(private val scope: CoroutineScope) {

    private val sampleRate = 22050

    fun playSound(type: MemeSoundType) {
        scope.launch(Dispatchers.IO) {
            try {
                val pcm = when (type) {
                    MemeSoundType.EVIL_LAUGH -> generateEvilLaugh()
                    MemeSoundType.QUACK_DUCK -> generateQuack()
                    MemeSoundType.CARTOON_BOOM -> generateBoom()
                    MemeSoundType.ALIEN_SIREN -> generateAlienSiren()
                    MemeSoundType.ZAGHRUTA_HORN -> generateHornFanfare()
                    MemeSoundType.MEMO_HELLO -> generateCartoonHello()
                    MemeSoundType.ROYAL_FANFARE -> generateRoyalFanfare()
                    MemeSoundType.EARTHQUAKE_RUMBLE -> generateEarthquake()
                    MemeSoundType.TOMATO_SPLAT -> generateTomatoSplat()
                    MemeSoundType.CRICKET_CHIRP -> generateCricket()
                    MemeSoundType.POLICE_SIREN -> generatePoliceSiren()
                    MemeSoundType.APPLAUSE_CHEER -> generateApplause()
                }

                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(pcm.size * 2)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()

                // Cleanup after duration
                val durationMs = (pcm.size * 1000L) / sampleRate + 200
                kotlinx.coroutines.delay(durationMs)
                track.stop()
                track.release()
            } catch (e: Exception) {
                // Audio synthesis fallback
            }
        }
    }

    private fun generateEvilLaugh(): ShortArray {
        val totalDurationMs = 1200
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)

        val chuckles = 5
        val samplesPerChuckle = numSamples / chuckles
        for (i in 0 until numSamples) {
            val chuckleIndex = i / samplesPerChuckle
            val posInChuckle = i % samplesPerChuckle
            val envelope = (1.0 - (posInChuckle.toDouble() / samplesPerChuckle)).coerceIn(0.0, 1.0)
            val freq = 180.0 - (chuckleIndex * 15.0) + (sin(i * 0.05) * 20.0)
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * envelope * 24000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateQuack(): ShortArray {
        val totalDurationMs = 700
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = if (progress < 0.2) progress / 0.2 else (1.0 - progress) / 0.8
            val freq = 450.0 + sin(i * 0.08) * 120.0 - (progress * 100.0)
            val sample = (sin(2.0 * Math.PI * freq * i / sampleRate) + 0.5 * sin(4.0 * Math.PI * freq * i / sampleRate)) * envelope * 20000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateBoom(): ShortArray {
        val totalDurationMs = 900
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = Math.exp(-progress * 4.5)
            val freq = 160.0 * Math.exp(-progress * 3.0) + 40.0
            val noise = (kotlin.random.Random.nextDouble() * 2.0 - 1.0) * 0.35
            val sample = (sin(2.0 * Math.PI * freq * i / sampleRate) + noise) * envelope * 28000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateAlienSiren(): ShortArray {
        val totalDurationMs = 1000
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val freq = 500.0 + sin(i * 0.035) * 350.0
            val envelope = (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0)
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * envelope * 22000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateHornFanfare(): ShortArray {
        val totalDurationMs = 1100
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)

        val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
        val samplesPerNote = numSamples / notes.size
        for (i in 0 until numSamples) {
            val noteIndex = (i / samplesPerNote).coerceIn(0, notes.size - 1)
            val freq = notes[noteIndex]
            val envelope = 0.8
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * envelope * 23000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateCartoonHello(): ShortArray {
        val totalDurationMs = 800
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val freq = if (i < numSamples / 2) 440.0 else 880.0
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * 0.7 * 20000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateRoyalFanfare(): ShortArray {
        val totalDurationMs = 1800
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)
        // Grand fanfare notes: C5, G5, C6, E6, G6
        val notes = listOf(523.25, 783.99, 1046.50, 1318.51, 1567.98)
        val samplesPerNote = numSamples / notes.size
        for (i in 0 until numSamples) {
            val noteIndex = (i / samplesPerNote).coerceIn(0, notes.size - 1)
            val freq = notes[noteIndex]
            val progress = (i % samplesPerNote).toDouble() / samplesPerNote
            val envelope = if (progress < 0.1) progress / 0.1 else Math.exp(-progress * 1.5)
            val harmonic = sin(2.0 * Math.PI * freq * 2 * i / sampleRate) * 0.3
            val sample = (sin(2.0 * Math.PI * freq * i / sampleRate) + harmonic) * envelope * 27000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateEarthquake(): ShortArray {
        val totalDurationMs = 1500
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val rumbleFreq = 45.0 + sin(i * 0.02) * 20.0
            val rumble = sin(2.0 * Math.PI * rumbleFreq * i / sampleRate)
            val noise = (kotlin.random.Random.nextDouble() * 2.0 - 1.0) * 0.6
            val envelope = sin(progress * Math.PI)
            val sample = (rumble * 0.6 + noise * 0.4) * envelope * 29000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateTomatoSplat(): ShortArray {
        val totalDurationMs = 600
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val squishFreq = 300.0 * Math.exp(-progress * 6.0) + 80.0
            val noise = (kotlin.random.Random.nextDouble() * 2.0 - 1.0) * 0.5
            val envelope = Math.exp(-progress * 5.0)
            val sample = (sin(2.0 * Math.PI * squishFreq * i / sampleRate) + noise) * envelope * 26000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateCricket(): ShortArray {
        val totalDurationMs = 1200
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val chirpCycle = (i % 2500) < 1200
            val freq = 4200.0
            val envelope = if (chirpCycle) 0.5 else 0.0
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * envelope * 18000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generatePoliceSiren(): ShortArray {
        val totalDurationMs = 1400
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val cycle = (i.toDouble() / sampleRate) * 2.5
            val freq = 650.0 + sin(cycle * 2.0 * Math.PI) * 300.0
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * 0.8 * 24000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }

    private fun generateApplause(): ShortArray {
        val totalDurationMs = 1600
        val numSamples = (sampleRate * totalDurationMs / 1000)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = if (progress < 0.2) progress / 0.2 else (1.0 - progress) / 0.8
            val clapNoise = if (kotlin.random.Random.nextDouble() > 0.85) (kotlin.random.Random.nextDouble() * 2.0 - 1.0) else 0.0
            val sample = clapNoise * envelope * 28000
            buffer[i] = sample.toInt().toShort()
        }
        return buffer
    }
}
