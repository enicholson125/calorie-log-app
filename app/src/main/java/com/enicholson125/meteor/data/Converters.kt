package com.enicholson125.meteor.data

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {
    private val dateTimeFormat = "yyyy-MM-dd HH:mm:ss"

    @RequiresApi(Build.VERSION_CODES.N)
    @TypeConverter
    fun dateTimeToString(value: Date): String {
        return SimpleDateFormat(dateTimeFormat).format(value)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @TypeConverter
    fun stringToDateTime(value: String): Date {
        return SimpleDateFormat(dateTimeFormat).parse(value)
    }
}
