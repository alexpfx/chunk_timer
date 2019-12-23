package dev.alessi.chunk.pomodoro.timer.android.ui.estimate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.alessi.chunk.pomodoro.timer.android.repository.EstimateRepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.repository.EstimationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstimateViewModel(val app: Application) : AndroidViewModel(app) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _allEstimatives = MutableLiveData<List<SizeTimeCountTO>>()
    private val _allEstimativesFor = MutableLiveData<SizeTimeCountTO>()


    val onAllEstimativesLoaded: LiveData<List<SizeTimeCountTO>>
        get() = _allEstimatives


    val onAllEstimativesFor: LiveData<SizeTimeCountTO>
        get() = _allEstimativesFor


    fun storeEstimative(estimative: WorkUnit) {

        scope.launch {
            val id = withContext(Dispatchers.IO) {
                getRepository().storeEstimation(estimative.copy(estimative = 1))
            }

            val sizeTimeCountTO = withContext(Dispatchers.IO) {
                getRepository().countAllSimilarEstimations(estimative.taskId, estimative)
            }

            _allEstimativesFor.value = sizeTimeCountTO

        }

    }


    fun removeEstimative(estimative: WorkUnit) {

        scope.launch {

            withContext(Dispatchers.IO) {
                getRepository().removeEstimation(estimative.copy(estimative = 1))
            }

            val sizeTimeCountTO: SizeTimeCountTO? = withContext(Dispatchers.IO) {
                getRepository().countAllSimilarEstimations(estimative.taskId, estimative)
            }

            _allEstimativesFor.value = sizeTimeCountTO?.run {
                sizeTimeCountTO
            } ?: SizeTimeCountTO.zero(estimative.sizeId, estimative.timeMinutes)

        }

    }

    fun removeAllEstimatives(workUnit: WorkUnit) {
        scope.launch {
            withContext(Dispatchers.IO){
                getRepository().removeAllSimilarEstimations(workUnit)
            }

            val estimatives = withContext(Dispatchers.IO){
                getRepository().countAllEstimationsFromTask(workUnit.taskId)
            }

            _allEstimatives.value = estimatives
        }

    }


    fun loadAllEstimatives(taskId: Int) {
        scope.launch {
            val estimatives = withContext(Dispatchers.IO) {
                getRepository().countAllEstimationsFromTask(taskId)
            }

            _allEstimatives.value = estimatives
        }

    }



    private fun getRepository(): EstimationRepository {
        return (app as EstimateRepositoryProvider).estimationRepository
    }


}