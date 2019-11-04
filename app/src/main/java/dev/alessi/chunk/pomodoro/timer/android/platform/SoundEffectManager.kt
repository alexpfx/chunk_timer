package dev.alessi.chunk.pomodoro.timer.android.platform

import android.content.Context
import android.media.MediaPlayer
import dev.alessi.chunk.pomodoro.timer.android.R

class SoundEffectManager(val context: Context) {
    private val finishAudio: MediaPlayer = MediaPlayer.create(context,
        R.raw.audio01
    )
    private val startAudio: MediaPlayer = MediaPlayer.create(context,
        R.raw.start
    )
    private val tickAudio: MediaPlayer = MediaPlayer.create(context,
        R.raw.tick
    )

    private var finishEnabled = true
    private var tickEnabled = true

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

    fun dispose() {

        finishAudio.stop()
        finishAudio.release()

        startAudio.stop()
        startAudio.release()
        tickAudio.stop()
        tickAudio.release()


    }


}