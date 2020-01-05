package dev.alessi.chunk.pomodoro.timer.android.ui.estimate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
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

    private val mAllEstimations = MutableLiveData<List<SizeTimeCountTO>>()
    private val mAllEstimationsFor = MutableLiveData<SizeTimeCountTO>()


    val onAllEstimationsLoaded: LiveData<List<SizeTimeCountTO>>
        get() = mAllEstimations


    val onAllEstimationsFor: LiveData<SizeTimeCountTO>
        get() = mAllEstimationsFor


    fun storeEstimation(estimation: WorkUnit) {
        scope.launch {
            val id = withContext(Dispatchers.IO) {
                getRepository().storeEstimation(estimation.copy(estimation = 1))
            }

            val sizeTimeCountTO = withContext(Dispatchers.IO) {
                getRepository().countAllSimilarEstimations(estimation.taskId, estimation)
            }

            mAllEstimationsFor.value = sizeTimeCountTO

        }

    }


    fun removeEstimation(estimation: WorkUnit) {

        scope.launch {

            withContext(Dispatchers.IO) {
                getRepository().removeEstimation(estimation.copy(estimation = 1))
            }

            val sizeTimeCountTO: SizeTimeCountTO? = withContext(Dispatchers.IO) {
                getRepository().countAllSimilarEstimations(estimation.taskId, estimation)
            }

            mAllEstimationsFor.value = sizeTimeCountTO?.run {
                sizeTimeCountTO
            } ?: SizeTimeCountTO.zero(estimation.sizeId, estimation.timeMinutes)

        }

    }

    fun removeAllEstimations(workUnit: WorkUnit) {
        scope.launch {
            withContext(Dispatchers.IO){
                getRepository().removeAllSimilarEstimations(workUnit)
            }

            val estimations = withContext(Dispatchers.IO){
                getRepository().countAllEstimationsFromTask(workUnit.taskId)
            }

            mAllEstimations.value = estimations
        }

    }


    fun loadAllEstimations(taskId: Int) {
        scope.launch {
            val estimation: List<SizeTimeCountTO> = withContext(Dispatchers.IO) {
                getRepository().countAllEstimationsFromTask(taskId)
            }

            mAllEstimations.value = estimation
        }

    }



    private fun getRepository(): EstimationRepository {
        return (app as EstimateRepositoryProvider).estimationRepository
    }


}