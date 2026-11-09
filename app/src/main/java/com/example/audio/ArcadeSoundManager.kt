package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Self-contained 8-bit Arcade Sound Synthesizer using AudioTrack.
 * Generates authentic retro arcade square/triangle wave chimes without external audio assets.
 */
class ArcadeSoundManager {
  private val scope = CoroutineScope(Dispatchers.Default)
  private val sampleRate = 22050

  var isMuted: Boolean = false

  fun playMove() {
    if (isMuted) return
    scope.launch {
      playTone(freq = 380.0, durationMs = 28, waveform = Waveform.SQUARE, volume = 0.35f)
    }
  }

  fun playRotate() {
    if (isMuted) return
    scope.launch {
      val b1 = generateToneBuffer(520.0, 22, Waveform.SQUARE, 0.4f)
      val b2 = generateToneBuffer(780.0, 28, Waveform.SQUARE, 0.45f)
      playRawBuffer(b1 + b2)
    }
  }

  fun playSoftDrop() {
    if (isMuted) return
    scope.launch {
      playTone(freq = 220.0, durationMs = 25, waveform = Waveform.TRIANGLE, volume = 0.3f)
    }
  }

  fun playHardDrop() {
    if (isMuted) return
    scope.launch {
      val totalSamples = (sampleRate * 0.08).toInt()
      val buffer = ShortArray(totalSamples)
      for (i in 0 until totalSamples) {
        val t = i.toDouble() / sampleRate
        val progress = i.toDouble() / totalSamples
        val freq = 200.0 - progress * 130.0
        val sample = sin(2.0 * PI * freq * t)
        val envelope = (1.0 - progress) * 0.7
        buffer[i] = (sample * envelope * Short.MAX_VALUE).toInt().toShort()
      }
      playRawBuffer(buffer)
    }
  }

  fun playHold() {
    if (isMuted) return
    scope.launch {
      val b1 = generateToneBuffer(480.0, 25, Waveform.SQUARE, 0.35f)
      val b2 = generateToneBuffer(380.0, 25, Waveform.SQUARE, 0.35f)
      playRawBuffer(b1 + b2)
    }
  }

  fun playLineClear(lines: Int) {
    if (isMuted) return
    scope.launch {
      if (lines >= 4) {
        val notes = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
        val combined = mutableListOf<Short>()
        for (n in notes) {
          combined.addAll(generateToneBuffer(n, 60, Waveform.SQUARE, 0.5f).toList())
        }
        playRawBuffer(combined.toShortArray())
      } else {
        val notes = when (lines) {
          1 -> listOf(659.25, 880.0)
          2 -> listOf(587.33, 783.99, 987.77)
          else -> listOf(523.25, 659.25, 783.99, 1046.50)
        }
        val combined = mutableListOf<Short>()
        for (n in notes) {
          combined.addAll(generateToneBuffer(n, 45, Waveform.SQUARE, 0.45f).toList())
        }
        playRawBuffer(combined.toShortArray())
      }
    }
  }

  fun playLevelUp() {
    if (isMuted) return
    scope.launch {
      val notes = listOf(440.0, 554.37, 659.25, 880.0)
      val combined = mutableListOf<Short>()
      for (n in notes) {
        combined.addAll(generateToneBuffer(n, 70, Waveform.SQUARE, 0.55f).toList())
      }
      playRawBuffer(combined.toShortArray())
    }
  }

  fun playGameOver() {
    if (isMuted) return
    scope.launch {
      val notes = listOf(587.33, 523.25, 466.16, 392.00, 311.13)
      val combined = mutableListOf<Short>()
      for (n in notes) {
        combined.addAll(generateToneBuffer(n, 120, Waveform.SQUARE, 0.45f).toList())
      }
      playRawBuffer(combined.toShortArray())
    }
  }

  private enum class Waveform { SQUARE, TRIANGLE }

  private fun generateToneBuffer(
    freq: Double,
    durationMs: Int,
    waveform: Waveform,
    volume: Float
  ): ShortArray {
    val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
    val buffer = ShortArray(totalSamples)
    for (i in 0 until totalSamples) {
      val t = i.toDouble() / sampleRate
      val raw = when (waveform) {
        Waveform.SQUARE -> if (sin(2.0 * PI * freq * t) >= 0) 1.0 else -1.0
        Waveform.TRIANGLE -> {
          val p = (t * freq) % 1.0
          if (p < 0.5) (4.0 * p - 1.0) else (3.0 - 4.0 * p)
        }
      }
      val fade = (1.0 - (i.toDouble() / totalSamples))
      buffer[i] = (raw * fade * volume * Short.MAX_VALUE).toInt().toShort()
    }
    return buffer
  }

  private fun playTone(freq: Double, durationMs: Int, waveform: Waveform, volume: Float) {
    val buffer = generateToneBuffer(freq, durationMs, waveform, volume)
    playRawBuffer(buffer)
  }

  private fun playRawBuffer(buffer: ShortArray) {
    if (buffer.isEmpty()) return
    try {
      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
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
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(buffer, 0, buffer.size)
      audioTrack.play()
      scope.launch {
        kotlinx.coroutines.delay(buffer.size * 1000L / sampleRate + 50)
        audioTrack.release()
      }
    } catch (_: Exception) {
      // Ignored if device audio subsystem is busy
    }
  }
}
