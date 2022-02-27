package com.enicholson125.meteor.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.time.LocalDate
import java.util.*

@Entity(
    tableName = "calorie_log",
)
data class CalorieLog(
    @PrimaryKey()
    @ColumnInfo(name = "id")
    @NonNull
    var logID: String,

    @NonNull
    @ColumnInfo(name = "time_logged") val timeLogged: Date,

    @NonNull
    @ColumnInfo(name = "calories") val calories: Int,

    @ColumnInfo(name = "description") val description: String,
) {
    fun format(): String {
        return "${this.timeLogged} ${this.calories} ${this.description}"
    }
}
