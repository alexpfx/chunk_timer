package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.repository.SliceRepository
import dev.alessi.chunk.pomodoro.timer.android.repository.SliceRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerFinishViewModel(private val app: Application) : AndroidViewModel(app) {

    private val mSliceLoaded = MutableLiveData<WorkUnit>()

    private val mScope = CoroutineScope(Dispatchers.Main)

    fun loadSlice(id: Int) {
        mScope.launch {
            val slice = withContext(Dispatchers.IO) {
                sliceRepository.loadSlice(id)
            }

            mSliceLoaded.value = slice
        }

    }


    val onSliceLoaded: LiveData<WorkUnit>
        get() = mSliceLoaded


    private val sliceRepository: SliceRepository
        get() = (app as SliceRepositoryProvider).sliceRepository


}