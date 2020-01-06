package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.alessi.chunk.pomodoro.timer.android.App
import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.repository.EstimateRepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.repository.SliceRepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskTO
import dev.alessi.chunk.pomodoro.timer.android.util.beginningOfDay
import dev.alessi.chunk.pomodoro.timer.android.util.beginningOfMonth
import dev.alessi.chunk.pomodoro.timer.android.util.beginningOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LoadPeriodSummariesViewModel(app: Application) : AndroidViewModel(app) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _taskAndPeriods = MutableLiveData<SelectTaskTO>()

    private val _allTaskAndPeriods = MutableLiveData<List<SelectTaskTO>>()


    val onPeriodsLoadedObserver: LiveData<List<SelectTaskTO>>
        get() = _allTaskAndPeriods

    val onPeriodsFromTaskLoadedObserver: LiveData<SelectTaskTO>
        get() = _taskAndPeriods

    private val taskRepository
        get() = (getApplication<App>() as TaskRepositoryProvider).taskRepository

    private val sliceRepository
        get() = (getApplication<App>() as SliceRepositoryProvider).sliceRepository

    private val estimationRepository
        get() = (getApplication<App>() as EstimateRepositoryProvider).estimationRepository



    fun loadAllAndSummarizeAndEstimations() {
        scope.launch {
            val allSummariesTO = mutableListOf<SelectTaskTO>()

            val tasks = withContext(Dispatchers.IO) {
                return@withContext taskRepository.loadAllActive()
            }

            tasks.filter { task -> task.uid!! > 0 }.forEach { task ->
                val taskId = task.uid!!

                val doneSlices = withContext(Dispatchers.IO) {

                    return@withContext sliceRepository.loadAllSlicesFromTask(taskId)
                }

                val estimations = withContext(Dispatchers.IO){
                    return@withContext estimationRepository.countAllEstimationsFromTask(taskId)
                }



                val countMinutes = countMinutes(estimations)
                val summary = summarize(doneSlices, task).copy(estimationMinutes = countMinutes)


                allSummariesTO.add(summary)

            }




            _allTaskAndPeriods.value = allSummariesTO

        }

    }

    private fun countMinutes(estimations: List<SizeTimeCountTO>): Int {
        var minutes = 0
        estimations.forEach {
            minutes += it.timeMinutes
        }
        return minutes
    }


    fun loadAndSummarize(task: Task) {
        scope.launch {

            val slices = withContext(Dispatchers.IO) {
                return@withContext sliceRepository.loadAllSlicesFromTask(task.uid!!)
            }

            val summary = summarize(slices, task)

            _taskAndPeriods.value = summary

        }

    }

    private fun summarize(
        slices: List<WorkUnit>,
        task: Task
    ): SelectTaskTO {
        val now = Date()
        val day = now.beginningOfDay()
        val week = now.beginningOfWeek()
        val month = now.beginningOfMonth()

        val slicesToday = slices.filter { it.finishDate >= day }
        val slicesWeek = slices.filter { it.finishDate >= week }
        val slicesMonth = slices.filter { it.finishDate >= month }

        val pToday = PeriodSummaryTO.from(PeriodSummaryTO.Period.TODAY, slicesToday)
        val pWeek = PeriodSummaryTO.from(PeriodSummaryTO.Period.THIS_WEEK, slicesWeek)
        val pMonth = PeriodSummaryTO.from(PeriodSummaryTO.Period.THIS_MONTH, slicesMonth)
        val pAll = PeriodSummaryTO.from(PeriodSummaryTO.Period.ALL, slices.toList())


        return SelectTaskTO(task, listOf(pToday, pWeek, pMonth, pAll))
    }
}