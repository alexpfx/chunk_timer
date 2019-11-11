package dev.alessi.chunk.pomodoro.timer.android.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.platform.SoundEffectManager
import dev.alessi.chunk.pomodoro.timer.android.util.Command
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder
import kotlinx.android.synthetic.main.toolbar.*

class TimerActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener, ChunkTimerServiceControl {


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(controller.currentDestination?.id != controller.graph.startDestination)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)


    }

    override fun onDestroy() {


        super.onDestroy()
    }

    override fun onResume() {
        if (intent?.hasExtra(ChunkTimerService.extra_param_a_timer_was_finish) == true) {
            getNavController().navigate(R.id.timerFinishDialogFragment, intent.extras)
            intent?.removeExtra(ChunkTimerService.extra_param_a_timer_was_finish)
        }

        super.onResume()
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

        window.setBackgroundDrawableResource(R.drawable.pizza_background)
        setSupportActionBar(toolbar)

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
        if (item.itemId == R.id.home) {
            getNavController().popBackStack()
            return true
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
        task: Int, @Command command: Int
    ) {
        val bundle = Bundle()
        bundle.putLong(ChunkTimerService.extra_param_total_time_millis, totalTimeMillis)
//        bundle.putString(ChunkTimerService.extra_param_taskname, task)
        bundle.putInt(ChunkTimerService.extra_param_task_id, task)
        bundle.putInt(ChunkTimerService.extra_param_sizeIndex, index)

        val intent =
            IntentBuilder.getIntentForService(
                this,
                command,
                bundle
            )
        startService(intent)
    }

    override fun requestTick() {
        val intent =
            IntentBuilder.getIntentForService(
                this,
                Command.ACTION_REQUEST_TICK
            )
        startService(intent)
    }


    override fun requestStateUpdate() {

        val intent =
            IntentBuilder.getIntentForService(
                this,
                Command.ACTION_UPDATE_STATE
            )
        startService(intent)
    }

    override fun doStopService() {
        startService(
            IntentBuilder.getIntentForService(
                this,
                Command.ACTION_STOP
            )
        )
    }


}
