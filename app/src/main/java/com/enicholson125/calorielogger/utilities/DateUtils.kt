package com.enicholson125.calorielogger.utilities;

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object DateUtils {
    fun isSameDay(first: Date, second: Date): Boolean {
        val firstLocalDate: LocalDate = first.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val secondLocalDate: LocalDate = second.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return firstLocalDate.isEqual(secondLocalDate)
    }

    fun isDateToday(date: Date): Boolean {
        return isSameDay(getCurrentDateTime(), date)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun addOneDay(date: Date): Date {
        val c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.DATE, 1);
        return c.getTime()
    }
}
