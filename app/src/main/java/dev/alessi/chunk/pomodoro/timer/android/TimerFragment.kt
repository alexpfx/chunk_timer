package dev.alessi.chunk.pomodoro.timer.android


import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import dev.alessi.TimerControl
import dev.alessi.components.BadgedButton
import kotlinx.android.synthetic.main.fragment_timer.*

/**
 * A simple [Fragment] subclass.
 */
class TimerFragment : Fragment(), TimerView {
    override fun showBreakTimerStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showBreakTimerCanceled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val mTimerView: TimerView = this
    private lateinit var sizes: Map<Int, Int>
    private lateinit var mTimerIntent: Intent
    var mTotalTime: Long = 0
    var mTimeLeft: Long = 0
    private lateinit var sizeBtns: List<BadgedButton>
    private lateinit var mTimerControl: TimerControl
    private var mBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

            mBound = false

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChunkTimerService.LocalBinder

            mTimerControl = binder.getService()
            mBound = true

        }


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

        forAllSizeButtons { index, it ->
            it.badgeText = sizes[index].toString()
            it.setOnClickListener(::onSizeSetupBtnClick)
        }

        startService()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

    }


    override fun onResume() {
        activity?.registerReceiver(mTickReceiver, IntentFilter(ChunkTimerService.TICK_BROADCAST))

        Intent(
            context,
            ChunkTimerService::class.java
        ).also { intent -> activity?.bindService(intent, mConnection, 0) }

        super.onResume()
    }

    override fun onPause() {
        Log.d(this.tag, "onDetach")
        activity?.unregisterReceiver(mTickReceiver)
        activity?.unbindService(mConnection)
        super.onPause()
    }


    override fun showTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnStartTimer.visibility = View.INVISIBLE
        forAllSizeButtons { b -> b.isEnabled = false }
    }


    override fun showTimerCanceled() {
        btnCancelTimer.visibility = View.INVISIBLE
        btnStartTimer.visibility = View.VISIBLE

        forAllSizeButtons { b -> b.isEnabled = true }


    }

    override fun showSizeSelected(index: Int) {
        forAllSizeButtons { it ->
            it.isChecked = (index == (it.tag as String).toInt())
        }

    }


    override fun showTick(state: Int, timeLeft: Long) {
        mTimeLeft = timeLeft

        updateTimerValue()

    }


    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val timeLeft = intent.getLongExtra(ChunkTimerService.EXTRA_TIME_LEFT, 0)
            val currentState = intent.getIntExtra(ChunkTimerService.EXTRA_TIME_CURRENT_STATE, 0)
            mTimerView.showTick(currentState, timeLeft)

        }
    }

    private fun onStartTimerClick(view: View) {
        execute { mTimerControl.startTimer() }

//        startService()
//        mTimerView.showTimerStarted()

    }

    private fun startService() {
        mTimerIntent = Intent(this.context, ChunkTimerService::class.java)
        mTimerIntent.putExtra(
            ChunkTimerService.EXTRA_ACTION,
            ChunkTimerService.STATE_RUNNING_POMODORO
        )
        mTimerIntent.putExtra(ChunkTimerService.EXTRA_TOTAL_TIME, mTotalTime)

        activity?.startService(mTimerIntent)

    }

    private fun onCancelTimerClick(view: View) {
        stopTimer()
        mTimerView.showTimerCanceled()

    }

    private fun stopTimer() {
        val stopped = activity?.stopService(mTimerIntent)

        resetTimer()
    }

    private fun resetTimer() {
        mTimeLeft = mTotalTime
        updateTimerValue()
    }

    private fun onSizeSetupBtnClick(view: View) {
        val index = (view.tag as String).toInt()
        mTotalTime = ((sizes[index] ?: error("wrong parameters")) * 60 * 1000).toLong()
        mTimerView.showSizeSelected(index)
        resetTimer()

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


    private fun forAllSizeButtons(action: (Int, BadgedButton) -> Unit) {
        sizeBtns.forEachIndexed(action)
    }

    private fun forAllSizeButtons(action: (BadgedButton) -> Unit) {
        sizeBtns.onEach(action)
    }

    private fun updateTimerValue() {
        val formatedTime = DateUtils.formatElapsedTime(mTimeLeft / 1000)
        txtTimerDisplay.text = formatedTime


        val percFinish = (mTimeLeft) * 100 / mTotalTime
        chip2.text = percFinish.toString().plus(" %")
        progressBar.progress = percFinish.toInt()
    }

    fun execute(command : () -> Unit){
        if (mBound){
            command()
        }
    }

}
