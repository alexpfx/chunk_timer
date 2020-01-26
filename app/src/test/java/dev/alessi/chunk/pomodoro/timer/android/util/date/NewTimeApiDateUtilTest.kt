package dev.alessi.chunk.pomodoro.timer.android.util.date

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class NewTimeApiDateUtilTest {

    private val newTimeApiDateUtil = NewTimeApiDateUtil()

    private val baseDate = getDateTime("20200125 1503")

    @Before
    fun setUp() {

    }

    private fun getDateTime(dateStr: String): Date{
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd HHmm")
        return simpleDateFormat.parse(dateStr)
    }

    @Test
    fun startOfDay() {
        val r = newTimeApiDateUtil.startOfDay(baseDate)
        Assert.assertEquals(r, getDateTime("20200125 0000"))
    }

    @Test
    fun startOfWeek() {
        val r = newTimeApiDateUtil.startOfWeek(baseDate)
        Assert.assertEquals(r, getDateTime("20200120 0000"))
    }

    @Test
    fun startOfMonth() {
        val r = newTimeApiDateUtil.startOfMonth(baseDate)
        Assert.assertEquals(r, getDateTime("20200101 0000"))
    }

    @Test
    fun addDaysTo() {
        val r = newTimeApiDateUtil.addDaysTo(baseDate, -1)
        Assert.assertEquals(r, getDateTime("20200124 1503"))
    }
}