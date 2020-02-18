package dev.alessi.chunk.pomodoro.timer.android.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.databinding.FragmentSettingsBinding
import dev.alessi.chunk.pomodoro.timer.android.service.SoundEffectManager
import dev.alessi.chunk.pomodoro.timer.android.ui.MainActivityControlViewModel

class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {


    private lateinit var sfm: SoundEffectManager
    private var mTimerRingIndex = -1
    private var mBreaktimeRingIndex = -1
    private val mainActivityControlViewModel: MainActivityControlViewModel by viewModels(::requireActivity)
    private var mSound: Int = -1
    private lateinit var mPreference: SharedPreferences
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onResume() {
        mainActivityControlViewModel.updateTitle(getString(R.string.settings))
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPreference = PreferenceManager.getDefaultSharedPreferences(this.context)

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
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return _binding?.root
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
            binding.spinnerRingingTimer.adapter = it
        }

        ArrayAdapter.createFromResource(
            this.activity!!,
            R.array.timer_sounds,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerRingingTimer.adapter = it
        }

        binding.spinnerRingingTimer.apply {
            isSelected = false
            setSelection(mTimerRingIndex, true)
            onItemSelectedListener = this@SettingsFragment
        }
        binding.spinnerRingingBreaktime.apply {
            isSelected = false
            setSelection(mBreaktimeRingIndex, true)
            onItemSelectedListener = this@SettingsFragment
        }

    }

    companion object {
        const val pref_ring_timer = "pref_ring_timer"
        const val pref_ring_breaktime = "pref_ring_breaktime"

    } // 178


}
