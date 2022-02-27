package com.enicholson125.calorielogger.data

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
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
