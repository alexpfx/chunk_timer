package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnitDao
import dev.alessi.chunk.pomodoro.timer.android.util.error

class EstimationRepositoryImpl(private val workUnitDao: WorkUnitDao) :
    EstimationRepository {


    override suspend fun storeEstimation(workUnit: WorkUnit): Int {
        try {
            return workUnitDao.insertWorkUnit(workUnit.copy(estimation = 1)).toInt()
        } catch (e: Exception) {
            error(e, "erro ao inserir work unit $workUnit")
            throw e
        }

    }

    override suspend fun removeEstimation(workUnit: WorkUnit) {
        try {
            println("removeWorkUnit $workUnit")
            workUnitDao.deleteFirstSimilar(
                workUnit.taskId,
                workUnit.sizeId,
                workUnit.timeMinutes,
                1
            )
        } catch (e: Exception) {
            error(e, "erro ao remover work unit $workUnit")
            throw e
        }
    }

    override suspend fun removeAllSimilarEstimations(estimation: WorkUnit) {
        try {
            workUnitDao.deleteAllSimilar(
                estimation.taskId,
                estimation.sizeId,
                estimation.timeMinutes,
                1
            )
        } catch (e: Exception) {
            error(e, "erro ao remover work unit $estimation")
            throw e
        }
    }


    override suspend fun countAllSimilarEstimations(
        taskId: Int,
        workUnit: WorkUnit
    ): SizeTimeCountTO {
        return workUnitDao.countAllSimilar(
            taskId,
            workUnit.sizeId,
            workUnit.timeMinutes,
            estimation = 1
        )
    }

    override suspend fun countAllEstimationsFromTask(taskId: Int): List<SizeTimeCountTO> {
        return workUnitDao.countAllFromTask(taskId, estimation = 1)

    }


}