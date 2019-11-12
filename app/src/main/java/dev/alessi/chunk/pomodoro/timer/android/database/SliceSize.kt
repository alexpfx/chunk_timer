package dev.alessi.chunk.pomodoro.timer.android.database

val sizeNames = listOf("PP", "P", "M", "G", "GG")


annotation class SizeIndex{

    companion object{
        const val PP = 0
        const val P = 1
        const val M = 2
        const val G = 3
        const val GG = 4
    }
}