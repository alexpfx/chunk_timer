package dev.alessi.chunk.pomodoro.timer.android.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.databinding.ActivityTimerBinding
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.ui.dialog.TimerFinishViewModel
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder


class TimerActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener, ChunkTimerServiceControl {


    private var _binding: ActivityTimerBinding? = null

    private val binding get () = _binding!!
    private val mActivityControlViewModel: MainActivityControlViewModel by viewModels()

    private val mTimerFinishViewModel: TimerFinishViewModel by viewModels()
    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(controller.currentDestination?.id != controller.graph.startDestination)
    }

    private fun showTimeSliceFinish(intent: Intent) {

        findNavController(R.id.nav_host_fragment).navigate(R.id.timerFinishDialogFragment, intent.extras)

    }

    private fun showBreaktimeFinish(intent: Intent) {
        getNavController().navigate(R.id.timerFinishDialogFragment, intent.extras)
    }

    fun getNavController(intent: Intent): NavController{
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)


        if (intent?.hasExtra(ChunkTimerService.extra_param_event) == true) {

            when (IntentBuilder.getEvent(intent)) {
                ChunkTimerService.Event.ON_BREAKTIME_COMPLETED -> showBreaktimeFinish(intent)
                ChunkTimerService.Event.ON_TIME_SLICE_COMPLETED -> {
                    showTimeSliceFinish(intent)
//                    doStopService()
                }
            }

            intent.removeExtra(ChunkTimerService.extra_param_event)

        }

    }

    override fun onStart() {
        super.onStart()

        getNavController().addOnDestinationChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        getNavController().removeOnDestinationChangedListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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
