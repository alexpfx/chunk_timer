package dev.alessi.chunk.pomodoro.timer.android.settings


import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.service.SoundEffectManager
import dev.alessi.chunk.pomodoro.timer.android.ui.MainSharedViewModel
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {


    private lateinit var sfm: SoundEffectManager


    companion object {
        const val pref_ring_timer = "pref_ring_timer"
        const val pref_ring_breaktime = "pref_ring_breaktime"

    }

    private var mTimerRingIndex = -1
    private var mBreaktimeRingIndex = -1
    private lateinit var mainSharedViewModel: MainSharedViewModel
    private var mSound: Int = -1
    private lateinit var mPreference: SharedPreferences

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPreference = PreferenceManager.getDefaultSharedPreferences(this.context)


        activity?.run {
            mainSharedViewModel = ViewModelProviders.of(this).get(MainSharedViewModel::class.java)
        } ?: throw Throwable("invalid activity")

        mainSharedViewModel.updateTitle(getString(R.string.settings))

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == pref_ring_timer || key == pref_ring_breaktime) {
            val sound = sharedPreferences?.getInt(key, -1)
            sound?.let { sfm.play(it) }
        }


    }

    override fun onStart() {
        super.onStart()
        mPreference.registerOnSharedPreferenceChangeListener(this)


    }

    override fun onStop() {
        mPreference.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        mSound = position - 1

        var prefKey = "0"

        when (parent?.tag) {
            getString(R.string.tag_ringing_timer) -> prefKey = pref_ring_timer
            getString(R.string.tag_ringing_breaktime) -> prefKey = pref_ring_breaktime
        }


        mPreference.edit().putInt(prefKey, mSound).apply()


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sfm = SoundEffectManager(this.context!!)

        loadPreferences()

        super.onCreate(savedInstanceState)
    }

    private fun loadPreferences() {
        mTimerRingIndex =
            PreferenceManager.getDefaultSharedPreferences(this.context).getInt(
                pref_ring_timer,
                -1
            ) + 1
        mBreaktimeRingIndex = PreferenceManager.getDefaultSharedPreferences(this.context).getInt(
            pref_ring_breaktime, -1
        ) + 1
    }

    override fun onDestroy() {
        sfm.dispose()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        createSpinners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createSpinners() {
        ArrayAdapter.createFromResource(
            this.activity!!,
            R.array.timer_sounds,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_ringing_timer.adapter = it
        }

        ArrayAdapter.createFromResource(
            this.activity!!,
            R.array.timer_sounds,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_ringing_breaktime.adapter = it
        }


        spinner_ringing_timer.isSelected = false
        spinner_ringing_breaktime.isSelected = false

        spinner_ringing_breaktime.setSelection(mBreaktimeRingIndex, true)
        spinner_ringing_timer.setSelection(mTimerRingIndex, true)

        spinner_ringing_breaktime.onItemSelectedListener = this
        spinner_ringing_timer.onItemSelectedListener = this

    }


}
