package dev.alessi.chunk.pomodoro.timer.android.domain

import android.content.SharedPreferences
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment

data class SizeValue(@SizeIndex val index: Int, val minutes: Int) {

    companion object {
        private val gson: Gson = Gson()

        fun loadMapFromPreferences(sharedPreferences: SharedPreferences): Map<Int, SizeValue> {

            val values = mutableMapOf<Int, SizeValue>()

            val jsonSizes = sharedPreferences.getString(
                TimerFragment.KEY_SIZE_JSON_ARRAY,
                TimerFragment.DEFAULT_JSON_SIZES
            )
            val sizesArray = gson.fromJson(jsonSizes, Array<Int>::class.java)

            IntRange(SizeIndex.PP, SizeIndex.GG).forEach {
                values[it] = SizeValue(it, sizesArray[it])
            }

            return values

        }
    }
}




