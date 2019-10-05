package dev.alessi.chunk.pomodoro.timer.android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class TimerActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {



    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(controller.currentDestination?.id != controller.graph.startDestination)

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
        setContentView(R.layout.activity_pref_test)


    }

    override fun onSupportNavigateUp(): Boolean {
        return getNavController().navigateUp() || super.onSupportNavigateUp()

    }


    private fun getNavController(): NavController {
        return Navigation.findNavController(this, R.id.nav_host_fragment)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            getNavController().popBackStack()
            return true
        }

        if (item.itemId == R.id.item_settings) {
            getNavController().navigate(R.id.action_timerFragment_to_timerSettingsFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }



}
