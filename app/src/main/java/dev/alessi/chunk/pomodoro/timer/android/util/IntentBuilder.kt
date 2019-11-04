package dev.alessi.chunk.pomodoro.timer.android.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerActivity


const val KEY_COMMAND = "KEY_COMMAND"

class IntentBuilder {
    companion object {

        fun getIntentForAction(
            action: String,
            @Command command: Int,
            extras: Bundle? = null
        ): Intent{
            val intent = Intent(action)

            intent.putExtra(KEY_COMMAND, command)

            if (extras != null) {
                intent.putExtras(extras)
            }
            return intent
        }

        fun getIntentForActivity(
            context: Context,
            @Command command: Int,
            extras: Bundle? = null
        ): Intent {
            return getIntent(
                context,
                command,
                extras,
                TimerActivity::class.java
            )
        }

        fun getIntentForService(
            context: Context,
            @Command command: Int,
            extras: Bundle? = null
        ): Intent {
            return getIntent(
                context,
                command,
                extras,
                ChunkTimerService::class.java
            )
        }


        fun getIntent(
            context: Context,
            @Command command: Int,
            extras: Bundle? = null,
            clazz: Class<*>
        ): Intent {
            val intent = Intent(context, clazz)

            intent.putExtra(KEY_COMMAND, command)

            if (extras != null) {
                intent.putExtras(extras)
            }
            return intent
        }

        @Command
        fun getCommand(intent: Intent): Int {
            return intent.getIntExtra(
                KEY_COMMAND,
                Command.INVALID
            )
        }
    }


}


annotation class Command {
    companion object {
        const val INVALID = -1
        const val ACTION_STOP = 0
        const val ACTION_START_TIMER = 1
        const val ACTION_START_BREAK = 2
        const val ACTION_TICK = 3
        const val ACTION_UPDATE_STATE = 4
        const val ACTION_REQUEST_TICK = 5
        const val ACTION_FINISH_TIMER = 6
        const val ACTION_FINISH_BREAK = 7
    }
}




























