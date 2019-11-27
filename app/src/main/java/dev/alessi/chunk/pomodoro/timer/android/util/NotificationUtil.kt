package dev.alessi.chunk.pomodoro.timer.android.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.alessi.chunk.pomodoro.timer.android.App
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerActivity


private fun getBaseNotificationBuilder(context: Context): NotificationCompat.Builder {

    return NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_timer_black_24dp)
        .setAutoCancel(true)
        .setColorized(true)
        .setContentIntent(getCallActivityIntent(context))
        .setOnlyAlertOnce(true)
        .setOngoing(true)

}

fun NotificationManagerCompat.getForegroundNotification(context: Context): Notification {
    val builder = getBaseNotificationBuilder(context)
    cancelAll()
    return builder.build()
}

fun NotificationManagerCompat.notifyTimerFinish(context: Context){
    val builder = getBaseNotificationBuilder(context)
    builder.setContentTitle(context.getString(R.string.message_title_timer_finish))
    builder.setContentText(context.getString(R.string.message_content_timer_finish))
    builder.setOngoing(false)
    cancelAll()

    notify(App.NOTIFICATION_ID, builder.build())
}

fun NotificationManagerCompat.notifyBreakFinish(context: Context){
    val builder = getBaseNotificationBuilder(context)
    builder.setContentTitle(context.getString(R.string.message_title_timer_break_finish))
    builder.setContentText(context.getString(R.string.message_content_timer_break_finish))
    builder.setOngoing(false)



    cancelAll()

    notify(App.NOTIFICATION_ID, builder.build())
}


fun NotificationManagerCompat.notifyTick(
    timeToFinish: Long,
    context: Context, @ChunkTimerService.TimerState status: Int
) {

    val builder = getBaseNotificationBuilder(context)

    setRunningContentMessages(builder, status, timeToFinish, context)
    notify(App.NOTIFICATION_ID, builder.build()!!)

}

private fun getCallActivityIntent(context: Context): PendingIntent? {
    val contentIntent = Intent(context, TimerActivity::class.java)
    return PendingIntent.getActivity(
        context,
        App.NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

fun setRunningContentMessages(
    builder: NotificationCompat.Builder,
    status: Int,
    timeToFinish: Long,
    context: Context
) {
    when (status) {
        ChunkTimerService.TimerState.status_running_break -> {
            builder.setContentTitle(context.getString(R.string.message_title_timer_running_break) + ": ${timeToFinish.toFormatedTime()}")
            builder.setContentText(context.getString(R.string.message_content_timer_running_break))

        }
        ChunkTimerService.TimerState.status_running_timer -> {
            builder.setContentTitle(context.getString(R.string.message_title_timer_running) + ": ${timeToFinish.toFormatedTime()}")
            builder.setContentText(context.getString(R.string.message_content_title_timer_running))
        }

    }
}
