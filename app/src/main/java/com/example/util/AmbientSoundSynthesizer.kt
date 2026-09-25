package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Authentic ambient sound synthesis recreating the acoustic resonance
 * of Silk Road instruments (Dutor, Nay, and Caravan chimes).
 *
 * Uses decaying plucked string envelopes and soft aerophone harmonics
 * to produce a soothing, museum-grade acoustic environment behind the voice guide.
 */
object AmbientSoundSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isEnabled: Boolean = true

    @Volatile
    var volumeLevel: Float = 0.22f // Calibrated for gentle background ambience

    enum class AmbienceStyle(val title: String, val icon: String) {
        SHASHMAQOM("Dutor & Nay", "🪕"),
        CARAVAN("Cho'l Sabosi & Nay", "🏜️"),
        SERENE_HALL("Tarixiy Koshona", "🏛️")
    }

    @Volatile
    var currentStyle: AmbienceStyle = AmbienceStyle.SHASHMAQOM

    fun start() {
        if (playbackJob?.isActive == true) return

        playbackJob = scope.launch {
            try {
                val sampleRate = 44100
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(sampleRate / 2)

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
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                // Shashmaqom pentatonic notes (D3, F3, G3, A3, C4, D4, E4)
                val maqomNotes = doubleArrayOf(
                    146.83, // D3
                    174.61, // F3
                    196.00, // G3
                    220.00, // A3
                    261.63, // C4
                    293.66, // D4
                    329.63, // E4
                    349.23  // F4
                )

                val bufferSize = 2048
                val buffer = ShortArray(bufferSize * 2)

                var sampleIndex = 0L
                var noteTimer = 0
                var currentPluckFreq = maqomNotes[0]
                var pluckTime = 0.0
                var noteIndex = 0

                // Nay continuous breath phase
                var nayPhase = 0.0
                var nayFreq = 220.0
                var vibratoPhase = 0.0

                while (isActive) {
                    if (!isEnabled || volumeLevel <= 0.01f) {
                        Thread.sleep(60)
                        continue
                    }

                    for (i in 0 until bufferSize) {
                        val t = sampleIndex / sampleRate.toDouble()

                        // Note trigger every ~2.5 seconds for relaxing plucked dutor
                        if (noteTimer <= 0) {
                            noteIndex = (noteIndex + 1 + (Math.random() * 2).toInt()) % maqomNotes.size
                            currentPluckFreq = maqomNotes[noteIndex]
                            pluckTime = 0.0
                            noteTimer = (sampleRate * (2.2 + Math.random() * 0.8)).toInt()
                            nayFreq = maqomNotes[(noteIndex + 2) % maqomNotes.size]
                        } else {
                            noteTimer--
                        }

                        // 1. Dutor Plucked String Simulation (Decaying plucked harmonic series)
                        val pluckDecay = exp(-pluckTime * 1.8)
                        val dutorFund = sin(2.0 * PI * currentPluckFreq * pluckTime)
                        val dutorHarm1 = sin(2.0 * PI * currentPluckFreq * 2.0 * pluckTime) * 0.45
                        val dutorHarm2 = sin(2.0 * PI * currentPluckFreq * 3.0 * pluckTime) * 0.20
                        val dutorPluck = (dutorFund + dutorHarm1 + dutorHarm2) * pluckDecay * 0.65
                        pluckTime += 1.0 / sampleRate

                        // 2. Soft Nay Flute with Warm Vibrato
                        val vibrato = sin(vibratoPhase) * 2.8
                        val naySample = sin(nayPhase) * 0.35 + sin(nayPhase * 2.0) * 0.08
                        val naySwell = 0.65 + 0.35 * sin(2.0 * PI * 0.25 * t) // slow swelling breath
                        val naySound = naySample * naySwell * 0.40

                        nayPhase += 2.0 * PI * (nayFreq + vibrato) / sampleRate
                        vibratoPhase += 2.0 * PI * 4.5 / sampleRate // 4.5 Hz natural flute vibrato

                        // 3. Hall Reverberation & Stereo Panning
                        val leftSig = (dutorPluck * 0.85 + naySound * 0.65) * volumeLevel
                        val rightSig = (dutorPluck * 0.65 + naySound * 0.85) * volumeLevel

                        val leftInt = (leftSig * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        val rightInt = (rightSig * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())

                        buffer[i * 2] = leftInt.toShort()
                        buffer[i * 2 + 1] = rightInt.toShort()

                        sampleIndex++
                    }

                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("AmbientSynthesizer", "Ambient synthesis error: ${e.message}")
            }
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("AmbientSynthesizer", "Error stopping: ${e.message}")
        }
    }
}
