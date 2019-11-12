package dev.alessi.chunk.pomodoro.timer.android.database

import android.text.format.DateUtils


class TaskSummary(val task: Task) {

    private var mSeconds = 0L

    private var sizeMap: MutableMap<Int, Int>


    var sizeSummary = ""

    var timeSummary = ""


    init {
        val slices = task.slices

        sizeMap = mutableMapOf(
            SizeIndex.PP to 0,
            SizeIndex.P to 0,
            SizeIndex.M to 0,
            SizeIndex.G to 0,
            SizeIndex.GG to 0
        )

        var minutes = 0L

        slices?.forEach {
            val x = sizeMap[it.size]?.plus(1)!!
            sizeMap[it.size] = x

            minutes = minutes.plus(it.timeMinutes)
        }

        mSeconds = minutes * 60

        sizeSummary = sizeSummary()

        timeSummary = hoursSummary()


    }

    private fun sizeSummary(): String {
        val sb = StringBuilder()
        sizeMap.entries.forEach {
            if (it.value > 0) {
                sb.append("[").append(it.value).append(" ").append(sizeNames[it.key]).append("] ")
            }
        }
        return sb.toString()
    }

    private fun hoursSummary() = DateUtils.formatElapsedTime(mSeconds)

}