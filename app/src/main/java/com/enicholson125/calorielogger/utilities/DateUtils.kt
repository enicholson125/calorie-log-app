package com.enicholson125.calorielogger.utilities

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object DateUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun isSameDay(first: Date, second: Date): Boolean {
        val firstLocalDate: LocalDate = first.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val secondLocalDate: LocalDate = second.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return firstLocalDate.isEqual(secondLocalDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isDateToday(date: Date): Boolean {
        return isSameDay(getCurrentDateTime(), date)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getCurrentDate(): Date {
        val now = getCurrentDateTime()
        now.hours = 0
        now.minutes = 0
        now.seconds = 0
        return now
    }

    fun addOneDay(date: Date): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, 1)
        return c.time
    }
}
