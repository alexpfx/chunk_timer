package dev.alessi.chunk.pomodoro.timer.android

import timeMap
import java.util.concurrent.TimeUnit

class PomodoroPresenterImpl(val view: PomodoroView) : PomodoroPresenter {
    private var lastSize = 2
    private lateinit var timerHandler: TimerState

    override fun setup(size: Int) {
        lastSize = size
        val totalTimeMillis = fromMinutes(getMinutes(size))
        timerHandler = TimerState(
            totalTimeMillis,
            { view.showTick(toSeconds(totalTimeMillis), toSeconds(it)) },
            ::onFinish
        )
        timerHandler.setup(totalTimeMillis)

        view.showTimerSetted(toSeconds(totalTimeMillis))
        view.showSetupChanged(size)
        view.showStatusReady()
    }


    private fun onFinish() {
        view.showTimerFinished()
        toggleStatus()
    }

    override fun toggleStatus() {
        if (timerHandler.isRunning()) {
            timerHandler.reset()
            view.showStatusReady()

        } else {
            timerHandler.start()
            view.showStatusRunning()

        }


    }


    private fun getMinutes(size: Int): Long {
        return timeMap[size] ?: error("Never could be null $size")
    }

    private fun fromMinutes(minutes: Long): Long {
        return TimeUnit.MINUTES.toMillis(minutes)
    }

    private fun toSeconds(millis: Long): Long {
        return TimeUnit.MILLISECONDS.toSeconds(millis)
    }

}