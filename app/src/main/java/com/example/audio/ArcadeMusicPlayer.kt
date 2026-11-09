package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * 8-Bit Chiptune Synthesizer for the iconic original Tetris Theme A (Korobeiniki).
 * Synthesizes lead melody + counterpoint bass line seamlessly in real time with AudioTrack.
 */
class ArcadeMusicPlayer {
  private val scope = CoroutineScope(Dispatchers.Default)
  private val sampleRate = 22050
  private var musicJob: Job? = null
  private var audioTrack: AudioTrack? = null

  var isEnabled: Boolean = true
    set(value) {
      field = value
      if (!value) {
        stop()
      }
    }

  private data class Note(val freq: Double, val beats: Float, val bassFreq: Double = 0.0)

  // Frequencies in Hz
  companion object {
    private const val REST = 0.0
    private const val A3 = 220.00
    private const val B3 = 246.94
    private const val C4 = 261.63
    private const val D4 = 293.66
    private const val E4 = 329.63
    private const val F4 = 349.23
    private const val G4 = 392.00
    private const val GS4 = 415.30
    private const val A4 = 440.00
    private const val B4 = 493.88
    private const val C5 = 523.25
    private const val D5 = 587.33
    private const val E5 = 659.25
    private const val F5 = 698.46
    private const val G5 = 783.99
    private const val A5 = 880.00

    // Full Korobeiniki Theme A (Lead melody + Bass chords)
    private val THEME_A = listOf(
      // Measure 1
      Note(E5, 1.0f, A3),
      Note(B4, 0.5f, E4),
      Note(C5, 0.5f, A3),
      Note(D5, 1.0f, D4),
      Note(C5, 0.5f, A3),
      Note(B4, 0.5f, E4),

      // Measure 2
      Note(A4, 1.0f, A3),
      Note(A4, 0.5f, E4),
      Note(C5, 0.5f, A3),
      Note(E5, 1.0f, C4),
      Note(D5, 0.5f, G4),
      Note(C5, 0.5f, E4),

      // Measure 3
      Note(B4, 1.5f, GS4),
      Note(C5, 0.5f, E4),
      Note(D5, 1.0f, B3),
      Note(E5, 1.0f, E4),

      // Measure 4
      Note(C5, 1.0f, A3),
      Note(A4, 1.0f, E4),
      Note(A4, 1.5f, A3),
      Note(REST, 0.5f, REST),

      // Measure 5
      Note(D5, 1.0f, D4),
      Note(F5, 0.5f, A4),
      Note(A5, 1.0f, D4),
      Note(G5, 0.5f, A4),
      Note(F5, 0.5f, D4),

      // Measure 6
      Note(E5, 1.5f, C4),
      Note(C5, 0.5f, G4),
      Note(E5, 1.0f, C4),
      Note(D5, 0.5f, G4),
      Note(C5, 0.5f, C4),

      // Measure 7
      Note(B4, 1.0f, GS4),
      Note(B4, 0.5f, E4),
      Note(C5, 0.5f, GS4),
      Note(D5, 1.0f, B3),
      Note(E5, 1.0f, E4),

      // Measure 8
      Note(C5, 1.0f, A3),
      Note(A4, 1.0f, E4),
      Note(A4, 1.5f, A3),
      Note(REST, 0.5f, REST)
    )
  }

  fun start(bpm: Int = 135) {
    if (!isEnabled || musicJob?.isActive == true) return
    musicJob = scope.launch {
      val minBuf = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
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
        .setBufferSizeInBytes(minBuf * 2)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()

      audioTrack = track
      track.play()

      try {
        val beatDurationMs = (60000.0 / bpm)
        while (isActive && isEnabled) {
          for (note in THEME_A) {
            if (!isActive || !isEnabled) break
            val noteDurationMs = (note.beats * beatDurationMs).toInt()
            val pcm = renderNotePCM(note.freq, note.bassFreq, noteDurationMs)
            track.write(pcm, 0, pcm.size)
          }
        }
      } catch (_: Exception) {
      } finally {
        try {
          track.stop()
          track.release()
        } catch (_: Exception) {}
        audioTrack = null
      }
    }
  }

  fun stop() {
    musicJob?.cancel()
    musicJob = null
    try {
      audioTrack?.stop()
      audioTrack?.release()
    } catch (_: Exception) {}
    audioTrack = null
  }

  fun setSpeedForLevel(level: Int) {
    val bpm = (130 + (level - 1) * 5).coerceIn(130, 185)
    if (musicJob?.isActive == true) {
      stop()
      start(bpm)
    }
  }

  private fun renderNotePCM(leadFreq: Double, bassFreq: Double, durationMs: Int): ShortArray {
    val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
    val buffer = ShortArray(totalSamples)

    val gateRatio = 0.88 // 88% note, 12% staccato gap for authentic 8-bit cadence
    val activeSamples = (totalSamples * gateRatio).toInt()

    for (i in 0 until totalSamples) {
      if (i >= activeSamples || leadFreq == REST) {
        buffer[i] = 0
        continue
      }

      val t = i.toDouble() / sampleRate
      // Lead voice (Square wave 25% duty cycle - authentic NES sound)
      val leadPhase = (t * leadFreq) % 1.0
      val leadSample = if (leadPhase < 0.25) 1.0 else -1.0

      // Bass voice (Triangle wave)
      val bassSample = if (bassFreq > 0.0) {
        val bassPhase = (t * bassFreq) % 1.0
        if (bassPhase < 0.5) (4.0 * bassPhase - 1.0) else (3.0 - 4.0 * bassPhase)
      } else {
        0.0
      }

      // Mix: Lead 55%, Bass 35%
      val mixed = leadSample * 0.22 + bassSample * 0.16
      // Small envelope to eliminate pops
      val attack = (i / 100.0).coerceAtMost(1.0)
      val decay = ((activeSamples - i) / 100.0).coerceAtMost(1.0)
      val finalVal = (mixed * attack * decay * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
      buffer[i] = finalVal.toShort()
    }
    return buffer
  }
}
