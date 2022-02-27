package com.enicholson125.calorielogger.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

/**
 * The Data Access Object for the [CalorieLog] class.
 */
@Dao
interface CalorieLogDAO {
    @Query("SELECT SUM(calories) FROM calorie_log")
    fun getCalorieTotal(): LiveData<Int>

    @Query("SELECT id,time_logged,calories,description FROM calorie_log ORDER BY time_logged DESC LIMIT 10")
    fun getLatestTenCalorieLogs(): LiveData<List<CalorieLog>>

    // TODO: this should use ORDER BY so we don't get all of them
    // TODO: have a flag in the database for daily budget
    @Query("SELECT time_logged FROM calorie_log WHERE description='Daily Budget' ORDER BY time_logged DESC LIMIT 1")
    fun getLatestDailyBudgetTime(): LiveData<Date>

    @Insert
    suspend fun insertCalorieLogEntry(calorieLogEntry: CalorieLog)
}
