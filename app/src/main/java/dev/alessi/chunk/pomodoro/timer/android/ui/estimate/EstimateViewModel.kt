package dev.alessi.chunk.pomodoro.timer.android.ui.estimate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstimateViewModel(val app: Application) : AndroidViewModel(app) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _newEstimative: MutableLiveData<WorkUnit> = MutableLiveData()
    private val _allEstimatives = MutableLiveData<List<WorkUnit>>()

    val onAllEstimativesLoaded: LiveData<List<WorkUnit>>
        get() = _allEstimatives


    val onNewEstimative: LiveData<WorkUnit>
        get() = _newEstimative


    fun storeEstimative(estimative: WorkUnit) {

        scope.launch {
            val id = withContext(Dispatchers.IO) {
                getRepository().storeWorkUnit(estimative.copy(estimative = 1))
            }

            _newEstimative.value = estimative.copy(uid = id)

        }

    }

    fun loadAllEstimatives(taskId: Int){
        scope.launch {
            val estimatives = withContext(Dispatchers.IO){
                getRepository().allEstimativesFromTask(taskId)
            }

            _allEstimatives.value = estimatives
        }

    }


    private fun getRepository(): TaskRepository {
        return (app as RepositoryProvider).getTaskRepository()
    }


}