package dev.alessi.chunk.pomodoro.timer.android.ui

import dev.alessi.chunk.pomodoro.timer.android.base.MvpView
import dev.alessi.chunk.pomodoro.timer.android.base.Presenter

interface ChunkTimerPresenter : Presenter {

    companion object {
        val empty = object : ChunkTimerPresenter {
            override fun actionSetupTimes(timeStr: String) {

            }

            override fun actionSetupBreak(timeMinutes: Int) {

            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun actionSetup(index: Int) {

            }

            override fun actionStartTimer() {

            }

            override fun actionStartBreak() {

            }

            override fun actionStopTimer() {

            }

            override fun actionStopBreak() {

            }

            override fun bindView(mvpView: MvpView) {

            }

            override fun unbindView() {

            }
        }

    }


    fun actionSetup(index: Int)

    fun actionSetupBreak(timeMinutes: Int)

    fun actionStartTimer()

    fun actionStartBreak()

    fun actionStopTimer()

    fun actionStopBreak()


    fun actionSetupTimes(timeStr: String)
}