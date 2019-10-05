package dev.alessi.chunk.pomodoro.timer.android


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.badge.BadgeDrawable
import dev.alessi.components.BadgedButton
import kotlinx.android.synthetic.main.fragment_timer.*

/**
 * A simple [Fragment] subclass.
 */
class TimerFragment : Fragment(), TimerView {

    val mTimerView: TimerView = this
    private lateinit var sizes: Map<Int, Int>
    private lateinit var mTimerIntent: Intent
    var mTotalTime: Long = 0
    private lateinit var sizeBtns: List<BadgedButton>

    override fun showTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnStartTimer.visibility = View.INVISIBLE
        forAllSizeButtons { it.isEnabled = false }
    }


    private fun forAllSizeButtons(action: (BadgedButton) -> Unit) {
        sizeBtns.forEach(action)
    }

    override fun showTimerCanceled() {
        btnCancelTimer.visibility = View.INVISIBLE
        btnStartTimer.visibility = View.VISIBLE

        forAllSizeButtons { it.isEnabled = true }

    }

    override fun showSizeSelected(index: Int) {
        sizeBtns.onEach {
            it.isChecked = (index == (it.tag as String).toInt())
        }

    }


    private fun setTimerValue(timeMillis: Long) {
        val formatedTime = DateUtils.formatElapsedTime(timeMillis / 1000)
        txtTimerDisplay.text = formatedTime
    }


    override fun showTick(state: Int, timeLeft: Long, totalTime: Long) {
        val badge = BadgeDrawable.create(this.context!!)
        badge.number = 40

        setTimerValue(timeLeft)

        val percFinish = (timeLeft) * 100 / totalTime
        chip2.text = percFinish.toString().plus(" %")
        progressBar.progress = percFinish.toInt()

    }


    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val timeLeft = intent.getLongExtra(ChunkTimerService.EXTRA_TIME_LEFT, 0)
            val totalTime = intent.getLongExtra(ChunkTimerService.EXTRA_TOTAL_TIME, 0)
            val currentState = intent.getIntExtra(ChunkTimerService.EXTRA_TIME_CURRENT_STATE, 0)
            mTimerView.showTick(currentState, timeLeft, totalTime)

        }
    }

    fun onStartTimerClick(view: View) {
        startTimer()
        mTimerView.showTimerStarted()

    }

    private fun startTimer() {
        mTimerIntent = Intent(this.context, ChunkTimerService::class.java)
        mTimerIntent.putExtra(ChunkTimerService.EXTRA_ACTION, ChunkTimerService.EXTRA_ACTION_START)
        mTimerIntent.putExtra(ChunkTimerService.EXTRA_TOTAL_TIME, mTotalTime)

        activity?.startService(mTimerIntent)
    }

    fun onCancelTimerClick(view: View) {
        stopTimer()
        mTimerView.showTimerCanceled()

    }

    private fun stopTimer() {
        activity?.stopService(mTimerIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        syncSettings()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnStartTimer.setOnClickListener(::onStartTimerClick)
        btnCancelTimer.setOnClickListener(::onCancelTimerClick)
        sizeBtns =
            listOf<BadgedButton>(frBtnSizePP, frBtnSizeP, frBtnSizeM, frBtnSizeG, frBtnSizeGG)


        sizeBtns.forEachIndexed { index, it ->
            it.badgeText = sizes[index].toString()
            it.setOnClickListener(::onSizeSetupBtnClick)

        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onSizeSetupBtnClick(view: View) {
        val index = (view.tag as String).toInt()
        mTotalTime = ((sizes[index] ?: error("wrong parameters")) * 60 * 1000).toLong()
        mTimerView.showSizeSelected(index)
        setTimerValue(mTotalTime)
    }


    private fun syncSettings() {
        val p = PreferenceManager.getDefaultSharedPreferences(this.context)
        sizes = mapOf(
            0 to p.getInt(TimerSettingsFragment.KEY_TIME_PP, 18),
            1 to p.getInt(TimerSettingsFragment.KEY_TIME_P, 24),
            2 to p.getInt(TimerSettingsFragment.KEY_TIME_M, 36),
            3 to p.getInt(TimerSettingsFragment.KEY_TIME_G, 48),
            4 to p.getInt(TimerSettingsFragment.KEY_TIME_GG, 60)
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(this.tag, "onAttach")
        (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.registerReceiver(mTickReceiver, IntentFilter(ChunkTimerService.TICK_BROADCAST))

    }

    override fun onDetach() {
        Log.d(this.tag, "onDetach")
        activity?.unregisterReceiver(mTickReceiver)
        super.onDetach()
    }


}
