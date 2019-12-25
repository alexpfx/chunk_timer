package dev.alessi.chunk.pomodoro.timer.android.domain

import android.content.SharedPreferences
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment

data class SizeValue(@SizeIndex val index: Int, val minutes: Int): Comparable<SizeValue> {



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

    override fun toString(): String {
        return "SizeValue(index=$index, minutes=$minutes)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SizeValue

        if (index != other.index) return false
        if (minutes != other.minutes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + minutes
        return result
    }

    override fun compareTo(other: SizeValue): Int {
        return if (index == other.index){
            this.minutes.compareTo(other.minutes)
        }else{
            index.compareTo(other.index)
        }
    }


}




