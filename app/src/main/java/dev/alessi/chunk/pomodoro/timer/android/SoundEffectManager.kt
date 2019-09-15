package dev.alessi.chunk.pomodoro.timer.android

import android.content.Context
import android.media.MediaPlayer

class SoundEffectManager(val context: Context) {
    private val finishAudio: MediaPlayer = MediaPlayer.create(context, R.raw.finish2)
    private val startAudio: MediaPlayer = MediaPlayer.create(context, R.raw.start)
    private val tickAudio: MediaPlayer = MediaPlayer.create(context, R.raw.tick)

    private var finishEnabled = true
    private var tickEnabled = false
    private var startEnabled = true

    fun playFinish() {
        if (finishEnabled) finishAudio.start()
    }

    fun playStart() {
        if (startEnabled) startAudio.start()
    }

    fun playTick() {
        if (tickEnabled) tickAudio.start()
    }


}