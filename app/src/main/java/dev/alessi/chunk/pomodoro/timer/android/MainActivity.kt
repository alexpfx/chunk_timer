package dev.alessi.chunk.pomodoro.timer.android

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*


fun Any.debug(message: String) {
    Log.d(this.javaClass.simpleName, message)
}

//
class MainActivity : AppCompatActivity(), PomodoroView {


    private lateinit var sizeButtons: List<MaterialButton>
    private lateinit var presenter: PomodoroPresenter
    private lateinit var sfxManager: SoundEffectManager


    override fun onPause() {
        debug("on pause")
        super.onPause()
    }

    override fun onStop() {
        debug("on stop")
        super.onStop()
    }

    override fun onResume(){
        debug("on resume")
        super.onResume()
    }

    override fun onStart() {
        debug("on start")
        super.onStart()
    }

    override fun onDestroy() {
        debug("on destroy")

        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sfxManager = SoundEffectManager(this)

        viewInit()
        presenter = PomodoroPresenterImpl(this)
        presenter.setup(2)

        debug("on create")


    }

    fun onToggleActionClick(view: View) {
        presenter.toggleStatus()
    }

    private fun viewInit() {
        this.sizeButtons = listOf(
            btnSizePP,
            btnSizeP,
            btnSizeM,
            btnSizeG,
            btnSizeGG
        )
        initTags()
    }

    private fun initTags() {
        sizeButtons.forEachIndexed { index, button ->
            run {

                print(button.text)
                button.tag = index

            }
        }
    }


    override fun showTick(totalTimeInSeconds: Long, secondsToFinish: Long) {
        val formatedTime = DateUtils.formatElapsedTime(secondsToFinish)
        txtTimerDisplay.text = formatedTime
        sfxManager.playTick()
        val percFinish = secondsToFinish * 100 / totalTimeInSeconds
        progress(percFinish.toInt())

    }

    override fun showTimerSetted(seconds: Long) {
        val formatedTime = DateUtils.formatElapsedTime(seconds)
        txtTimerDisplay.text = formatedTime
        progress(100)
    }

    fun progress(value: Int) {
        progressBar.progress = value
    }


    override fun showTimerFinished() {
        sfxManager.playFinish()
    }


    override fun showSetupChanged(tag: Int) {
        uncheckAll()
        checkThis(tag)
    }

    fun changeSize(view: View) {
        presenter.setup(view.tag as Int)
    }

    override fun showStatusReady() {
        enableAll()
        btnTimerAction.setIconResource(R.drawable.ic_play_arrow_black_24dp)
        btnTimerAction.setText(R.string.button_label_start)

    }

    override fun showStatusRunning() {
        disableAll()
        btnTimerAction.setIconResource(R.drawable.ic_stop_black_24dp)
        btnTimerAction.setText(R.string.button_label_cancel)
        sfxManager.playStart()
    }


    private fun disableAll() {
        sizeButtons.forEach {
            it.isEnabled = false
        }
    }

    private fun enableAll() {
        sizeButtons.forEach {
            it.isEnabled = true
        }

    }

    private fun uncheckAll() {
        sizeButtons.forEach {
            it.isChecked = false
        }
    }

    private fun checkThis(tag: Int) {
        getSizedButtonByTagId(tag).isChecked = true
    }

    private fun getSizedButtonByTagId(id: Int): MaterialButton {
        return this.sizeButtons.first {
            it.tag == id
        }

    }


}
