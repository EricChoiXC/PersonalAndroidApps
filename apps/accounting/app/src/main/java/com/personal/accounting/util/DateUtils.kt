package com.personal.accounting.util

import com.personal.accounting.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val sDisplayFormat = SimpleDateFormat(Config.DATE_FORMAT_DISPLAY, Locale.getDefault())
    private val sDateTimeDisplayFormat = SimpleDateFormat(Config.DATE_TIME_FORMAT_DISPLAY, Locale.getDefault())
    private val sExportFormat = SimpleDateFormat(Config.DATE_FORMAT_EXPORT, Locale.getDefault())
    private val sYearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    private val sYearMonthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    fun formatDisplay(timestamp: Long): String {
        return sDisplayFormat.format(Date(timestamp))
    }

    fun formatDateTimeDisplay(timestamp: Long): String {
        return sDateTimeDisplayFormat.format(Date(timestamp))
    }

    fun formatYear(timestamp: Long): String {
        return sYearFormat.format(Date(timestamp))
    }

    fun formatYearMonth(timestamp: Long): String {
        return sYearMonthFormat.format(Date(timestamp))
    }

    fun getYearRange(year: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.YEAR, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis
        return Pair(start, end)
    }

    fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis
        return Pair(start, end)
    }

    fun isWithinOneYear(startDate: Long, endDate: Long): Boolean {
        return (endDate - startDate) <= Config.MAX_DATE_RANGE_DAYS * 24 * 60 * 60 * 1000L
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }
}
