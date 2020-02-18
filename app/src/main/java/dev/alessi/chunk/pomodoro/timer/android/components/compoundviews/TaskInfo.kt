package dev.alessi.chunk.pomodoro.timer.android.components.compoundviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import dev.alessi.chunk.pomodoro.timer.android.R

class TaskInfo (context: Context, attrs: AttributeSet?, defStyle: Int) : ConstraintLayout(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var statusColor: Int
    var status: String
    var lastChunkDate: String
    var startedDate: String
    var createdDate: String
    var description: String
    var name: String


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TaskInfo, -1, defStyle).apply {
            name = getString(R.styleable.TaskInfo_name) ?: ""
            description = getString(R.styleable.TaskInfo_description) ?: ""
            createdDate = getString(R.styleable.TaskInfo_createdDate) ?: ""
            startedDate = getString(R.styleable.TaskInfo_startedDate) ?: ""
            lastChunkDate = getString(R.styleable.TaskInfo_lastChunkDate) ?: ""
            status = getString(R.styleable.TaskInfo_status) ?: ""
            statusColor = getInt(R.styleable.TaskInfo_statusColor, -1)

            recycle()
        }


        LayoutInflater.from(context).inflate(R.layout.compound_task_info, this, true).apply {




        }

    }




}