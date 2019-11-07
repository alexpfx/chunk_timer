package dev.alessi.chunk.pomodoro.timer.android.platform

import android.content.Context
import android.media.MediaPlayer

import dev.alessi.chunk.pomodoro.timer.android.R

class SoundEffectManager(val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val audios = arrayOf(
        R.raw.audio01,
        R.raw.audio02,
        R.raw.audio03,
        R.raw.audio04,
        R.raw.audio05,
        R.raw.audio06
    )


    fun play(id: Int){
        if (id < audios.size && id >= 0){
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, audios[id])
            mediaPlayer?.start()
        }


    }



    fun dispose() {
        mediaPlayer?.release()
    }

}