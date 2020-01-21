package dev.alessi.chunk.pomodoro.timer.android.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.ui.dialog.TimerFinishViewModel
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder
import kotlinx.android.synthetic.main.toolbar.*

class TimerActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener, ChunkTimerServiceControl {


    private lateinit var mActivityControlViewModel: MainActivityControlViewModel
    private lateinit var mTimerFinishViewModel: TimerFinishViewModel

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(controller.currentDestination?.id != controller.graph.startDestination)
    }


    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


        }
    }

    private fun showTimeSliceFinish(intent: Intent) {

        findNavController(R.id.nav_host_fragment).navigate(R.id.timerFinishDialogFragment, intent.extras)

    }

    private fun showBreaktimeFinish(intent: Intent) {


        findNavController(R.id.nav_host_fragment).navigate(R.id.timerFinishDialogFragment, intent.extras)


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println("onNewIntent")

        if (intent?.hasExtra(ChunkTimerService.extra_param_event) == true) {

            val event = IntentBuilder.getEvent(intent)

            println("mTickReceiver $event")

            when (event) {
                ChunkTimerService.Event.ON_BREAKTIME_COMPLETED -> showBreaktimeFinish(intent)
                ChunkTimerService.Event.ON_TIME_SLICE_COMPLETED -> {
                    showTimeSliceFinish(intent)
//                    doStopService()
                }
            }

            intent.removeExtra(ChunkTimerService.extra_param_event)

        }

    }


    override fun onResume() {
        super.onResume()
        println("onResume")


    }

    override fun onPause() {
        super.onPause()

    }


    override fun onStart() {
        super.onStart()

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        findNavController(R.id.nav_host_fragment).removeOnDestinationChangedListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)


//        window.setBackgroundDrawableResource(R.drawable.pizza_background)
        setSupportActionBar(toolbar)


        mActivityControlViewModel = ViewModelProviders.of(this).get(MainActivityControlViewModel::class.java)

        mTimerFinishViewModel = ViewModelProviders.of(this).get(TimerFinishViewModel::class.java)

        mActivityControlViewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })

        mActivityControlViewModel.subtitle.observe(this, Observer {
            supportActionBar?.subtitle = it
        })


    }


    override fun onSupportNavigateUp(): Boolean {
        return getNavController().navigateUp() || super.onSupportNavigateUp()

    }


    private fun getNavController(): NavController {
        return Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                getNavController().popBackStack()
                return true
            }
            R.id.item_about_fragment -> {
                getNavController().navigate(R.id.aboutFragment)
                return true
            }
            R.id.item_settings_fragment -> {
                getNavController().navigate(R.id.settingsFragment)
                return true

            }
            R.id.item_setup_breaktime_dialog -> {
                getNavController().navigate(R.id.inputBreakTimeDialogFragment)
                return true

            }
            R.id.item_setup_timers_dialog -> {
                getNavController().navigate(R.id.timerSettingsDialogFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun doStartService(
        totalTimeMillis: Long,
        index: Int,
        task: Int, @ChunkTimerService.Command command: Int
    ) {
        val bundle = Bundle()
        bundle.putLong(ChunkTimerService.extra_param_total_time_millis, totalTimeMillis)
        bundle.putInt(ChunkTimerService.extra_param_task_id, task)
        bundle.putInt(ChunkTimerService.extra_param_sizeIndex, index)
        bundle.putInt(ChunkTimerService.extra_param_command, command)


        val intent =
            IntentBuilder.getIntentForService(
                this,
                command,
                bundle
            )

        startService(intent)
    }


    override fun requestStateUpdate() {
        val intent =
            IntentBuilder.getIntentForService(
                this,
                ChunkTimerService.Command.ACTION_REPEAT_LAST_EVENT
            )
        startService(intent)
    }

    override fun doStopService() {
        startService(
            IntentBuilder.getIntentForService(
                this,
                ChunkTimerService.Command.ACTION_STOP_SERVICE
            )
        )
    }


}
