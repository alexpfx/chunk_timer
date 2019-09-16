package dev.alessi.chunk.pomodoro.timer.android

import android.app.job.JobScheduler

class PomodoroPresenterImpl(val view: PomodoroView) : PomodoroPresenter {


    private val timeMap: Map<Int, Long> =
        mapOf(0 to 18L, 1 to 24L, 2 to 36L, 3 to 48L, 4 to 60L)
    private var minutes: Long = 0L
    private var lastSize = 2
    private var timer: ChunkCoutDownTimer =
        ChunkCoutDownTimer(
            getMinutes(lastSize),
            1,
            ::onTicket,
            ::onFinish
        )
    private var timerStatus: TimerStatus =
        TimerStatus.ready

    override fun putToSleep() {
        val s: JobScheduler


    }

    private fun onFinish() {
        view.showTimerFinished()
    }

    private fun onTicket(secondsToFinish: Long) {
        view.showTick(timer.totalTimeInMinutes * 60, secondsToFinish)
    }

    private fun start() {
        timer.start()
        view.showTimerStarted()
    }

    private fun cancel() {
        timer.cancel()
        setup(lastSize)
        view.showTimerCanceled()
    }

    private fun getMinutes(tag: Int): Long {
        return timeMap[tag] ?: error("Never could be null $tag")
    }

    override fun setup(tag: Int) {
        minutes = getMinutes(tag)
        lastSize = tag
        timer.cancel()
        timer = ChunkCoutDownTimer(
            this.minutes,
            1,
            ::onTicket,
            ::onFinish
        )
        timerStatus = TimerStatus.ready
        view.showTimerSetted(minutes * 60L)
        view.showSetupChanged(tag)
        view.showTimerCanceled()

    }

    private fun changeState(newStatus: TimerStatus) {
        this.timerStatus = newStatus
        view.showStateChanged(newStatus)
    }

    override fun toggleStatus() {
        if (timerStatus == TimerStatus.ready) {
            changeState(TimerStatus.running)
            start()
            view.showTimerStarted()
        } else {
            changeState(TimerStatus.ready)
            cancel()
            view.showTimerCanceled()
        }

    }
}