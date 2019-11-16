package dev.alessi.chunk.pomodoro.timer.android.database

import android.text.format.DateUtils


class TaskSummary(val task: Task) {

    private var mSeconds = 0L

    private var sizeMap: MutableMap<Int, Int>
    private var nameMap: MutableMap<Int, String> = mutableMapOf()

    companion object {
        const val hours_const_div = 3600
        const val min_const_div = 60
        const val seconds_const_div = 60
    }

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

        slices?.forEach { workUnit ->
            val x = sizeMap[workUnit.sizeId]?.plus(1)!!
            sizeMap[workUnit.sizeId] = x
            minutes = minutes.plus(workUnit.timeMinutes)
            nameMap[workUnit.sizeId] = workUnit.taskSize?.name!!
        }

        mSeconds = minutes * 60

        sizeSummary = sizeSummary()

        timeSummary = hoursSummary()


    }

    private fun sizeSummary(): String {
        val sb = StringBuilder()
        sizeMap.entries.forEachIndexed { index, it ->
            if (it.value > 0) {
                sb.append(" - ").append(it.value).append(" ").append(nameMap[it.key])
            }
        }

        return sb.toString().replaceFirst(" - ", "")
    }


    private fun hoursSummary(): String {
        val hours = mSeconds / hours_const_div
        var s = mSeconds.rem(hours_const_div)
        val minutes = s / min_const_div
        s = s.rem(min_const_div)
        val seconds = s / seconds_const_div
        return "${hours}h ${minutes.toString().padStart(2, '0')}m ${seconds.toString().padStart(
            2,
            '0'
        )}s"
    }

}