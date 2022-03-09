package com.enicholson125.calorielogger.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

    @ColumnInfo(name = "sweet", defaultValue = "1") val isSweet: Boolean,
) {
    fun format(): String {
        return "${this.timeLogged} ${this.calories} ${this.description} ${if (this.isSweet) "sweet" else "not-sweet" }"
    }
}
