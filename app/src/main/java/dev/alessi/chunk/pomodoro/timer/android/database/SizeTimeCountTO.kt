package dev.alessi.chunk.pomodoro.timer.android.database

data class SizeTimeCountTO(val sizeId: Int, val timeMinutes: Int, val count: Int) {


    companion object {
        fun zero(sizeId: Int, timeMinutes: Int): SizeTimeCountTO {
            return SizeTimeCountTO(sizeId = sizeId, timeMinutes = timeMinutes, count = 0)
        }
    }


}