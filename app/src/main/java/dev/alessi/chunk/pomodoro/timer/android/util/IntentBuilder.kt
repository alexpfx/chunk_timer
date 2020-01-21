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
            extras: Bundle? = null
        ): Intent {
            var intent = getIntent(
                context,
                extras,
                TimerActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }

        fun getIntentForService(
            context: Context,
            @ChunkTimerService.Command command: Int,
            extras: Bundle? = null
        ): Intent {
            val intent = getIntent(
                context,
                extras,
                ChunkTimerService::class.java
            )
            intent.putExtra(ChunkTimerService.extra_param_command, command)
            return intent
        }


        private fun getIntent(
            context: Context,
            extras: Bundle? = null,
            clazz: Class<*>
        ): Intent {
            val intent = Intent(context, clazz)

            if (extras != null) {
                intent.putExtras(extras)
            }
            return intent
        }

        fun getEvent(intent: Intent): @ChunkTimerService.Event Int {
            return intent.getIntExtra(ChunkTimerService.extra_param_event, -1)
        }

        fun getServiceExtras(extras: Bundle): IntentTimeExtras {
            return IntentTimeExtras(
                extras.getInt(ChunkTimerService.extra_param_event, -1),
                extras.getLong(ChunkTimerService.extra_param_current_time, -1),
                extras.getLong(ChunkTimerService.extra_param_total_time_millis, -1),
                extras.getInt(ChunkTimerService.extra_param_slice_id, -1),
                extras.getInt(ChunkTimerService.extra_param_tick_type, -1)
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


























