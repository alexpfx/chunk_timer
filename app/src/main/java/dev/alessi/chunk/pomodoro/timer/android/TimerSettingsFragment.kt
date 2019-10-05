package dev.alessi.chunk.pomodoro.timer.android

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SeekBarPreference


fun Preference.printTitle(message: String = "") {
    println(message)
    println(this.title)
}

fun Preference.printSummary(message: String = "") {
    println(message)
    println(this.summary)
}


class TimerSettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    private var fullTimePref: SeekBarPreference? = null

    private var intermSizePref: Preference? = null

    companion object {
        const val KEY_TIME_GG = "key_time_gg"
        const val KEY_TIME_G = "key_time_g"
        const val KEY_TIME_M = "key_time_m"
        const val KEY_TIME_P = "key_time_p"
        const val KEY_TIME_PP = "key_time_pp"
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        var fullTime = fullTimePref?.value

        if (preference?.key == "key_chunk_full_time") {
            fullTime = ((newValue!! as Int).div(10)) * 10
            updateSizes(fullTime)
            fullTimePref?.value = fullTime
        }

        return true
    }

    private fun updateSizes(fullTime: Int) {
        val gg = (fullTime)
        val g = (fullTime * 0.8).toInt()
        val m = (fullTime * 0.6).toInt()
        val p = (fullTime * 0.4).toInt()
        val pp = (fullTime * 0.3).toInt()

        intermSizePref?.summary = "[GG: $gg] [G: $g] [M: $m] [P: $p] [PP: $pp]"

        PreferenceManager.getDefaultSharedPreferences(this.context).edit().apply {
            this.putInt(KEY_TIME_GG, fullTime)
            this.putInt(KEY_TIME_G, g)
            this.putInt(KEY_TIME_M, m)
            this.putInt(KEY_TIME_P, p)
            this.putInt(KEY_TIME_PP, pp)
            this.apply()
        }


    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.chunk_timer_preference_screen, rootKey)

        init()
        updateSizes(fullTimePref?.value!!)

    }

    private fun init() {
        fullTimePref = findPreference("key_chunk_full_time")
        fullTimePref?.onPreferenceChangeListener = this



        intermSizePref = findPreference("key_other_chunk_sizes")


    }
}


