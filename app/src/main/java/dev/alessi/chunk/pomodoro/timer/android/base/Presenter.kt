package dev.alessi.chunk.pomodoro.timer.android.base

interface Presenter {
    fun bindView(mvpView: MvpView)
    fun unbindView()
    fun onResume()
    fun onPause()

}