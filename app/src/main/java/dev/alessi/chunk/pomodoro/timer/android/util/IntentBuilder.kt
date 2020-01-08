package dev.alessi.chunk.pomodoro.timer.android.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkCountDownTimer
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerActivity


class IntentBuilder {
    companion object {

        fun getIntentForEvent(
            action: String,
            @ChunkTimerService.Event event: Int,
            extras: Bundle? = null
        ): Intent {
            val intent = Intent(action)

            intent.putExtra(ChunkTimerService.extra_param_event, event)

            if (extras != null) {
                intent.putExtras(extras)
            }
            return intent
        }

        fun getIntentForActivity(
            context: Context,
            @ChunkTimerService.Command command: Int,
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
            @ChunkTimerService.Command command: Int,
            extras: Bundle? = null
        ): Intent {
            return getIntent(
                context,
                command,
                extras,
                ChunkTimerService::class.java
            )
        }


        private fun getIntent(
            context: Context,
            @ChunkTimerService.Command command: Int,
            extras: Bundle? = null,
            clazz: Class<*>
        ): Intent {
            val intent = Intent(context, clazz)

            intent.putExtra(ChunkTimerService.extra_param_command, command)

            if (extras != null) {
                intent.putExtras(extras)
            }
            return intent
        }

        fun getEvent(intent: Intent): @ChunkTimerService.Event Int {
            return intent.getIntExtra(ChunkTimerService.extra_param_event, -1)
        }


        fun getServiceExtras(intent: Intent): IntentTimeExtras {
            return IntentTimeExtras(
                intent.getIntExtra(ChunkTimerService.extra_param_event, -1),
                intent.getLongExtra(ChunkTimerService.extra_param_current_time, -1),
                intent.getLongExtra(ChunkTimerService.extra_param_total_time_millis, -1),
                intent.getIntExtra(ChunkTimerService.extra_param_slice_id, -1),
                intent.getIntExtra(ChunkTimerService.extra_param_tick_type, -1)
            )

        }

        fun getCommand(intent: Intent): @ChunkTimerService.Command Int {
            return intent.getIntExtra(
                ChunkTimerService.extra_param_command,
                ChunkTimerService.Command.INVALID
            )
        }
    }

    data class IntentTimeExtras(
        val event: Int,
        val currentTime: Long,
        val totalTime: Long,
        val sliceId: Int?,
        val tickType: @ChunkCountDownTimer.Type Int?
    )


}


























