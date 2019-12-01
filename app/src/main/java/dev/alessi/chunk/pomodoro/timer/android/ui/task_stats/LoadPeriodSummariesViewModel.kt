package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.util.beginningOfDay
import dev.alessi.chunk.pomodoro.timer.android.util.beginningOfMonth
import dev.alessi.chunk.pomodoro.timer.android.util.beginningOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.*

class LoadPeriodSummariesViewModel(private val mTaskRepository: TaskRepository) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _summaries = MutableLiveData<List<PeriodSummaryTO>>()

    val summaries: LiveData<List<PeriodSummaryTO>>
        get() = _summaries


    fun loadAndSummarize(taskId: Int) {
        scope.launch {
            val now = Date()
            val day = now.beginningOfDay()
            val week = now.beginningOfWeek()
            val month = now.beginningOfMonth()

            val slices = withContext(Dispatchers.IO) {
                return@withContext mTaskRepository.loadAllFromTask(taskId)
            }

            val slicesToday = slices.filter { it.finishDate >= day }
            val slicesWeek = slices.filter { it.finishDate >= week }
            val slicesMonth = slices.filter { it.finishDate >= month }

            val pToday = PeriodSummaryTO.from(PeriodSummaryTO.Period.TODAY, slicesToday)
            val pWeek = PeriodSummaryTO.from(PeriodSummaryTO.Period.THIS_WEEK, slicesWeek)
            val pMonth = PeriodSummaryTO.from(PeriodSummaryTO.Period.THIS_MONTH, slicesMonth)

            _summaries.value = listOf(pToday, pWeek, pMonth)
        }

    }
}