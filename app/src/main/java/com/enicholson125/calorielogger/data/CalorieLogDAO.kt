package com.enicholson125.calorielogger.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

/**
 * The Data Access Object for the [CalorieLog] class.
 */
@Dao
interface CalorieLogDAO {
    @Query("SELECT SUM(calories) FROM calorie_log WHERE sweet=:sweet")
    fun getCalorieTotal(sweet: Boolean): LiveData<Int>

    @Query("SELECT SUM(calories) FROM calorie_log WHERE sweet=:sweet AND time_logged >= :date AND description!='Daily Budget' AND description!='Daily Budget Full'")
    fun getDayCalorieTotal(date: Date, sweet: Boolean): LiveData<Int>

    @Query("SELECT id,time_logged,calories,description,sweet FROM calorie_log ORDER BY time_logged DESC LIMIT 10")
    fun getLatestTenCalorieLogs(): LiveData<List<CalorieLog>>

    @Query("SELECT id,time_logged,calories,description,sweet FROM calorie_log WHERE time_logged >= :date ORDER BY time_logged DESC")
    fun getCalorieLogsSinceDate(date: Date): LiveData<List<CalorieLog>>

    @Query("SELECT id,time_logged,calories,description,sweet FROM calorie_log WHERE description LIKE 'Daily Budget%' AND sweet=:sweet ORDER BY time_logged DESC LIMIT 1")
    fun getLatestDailyBudgetLog(sweet: Boolean): LiveData<CalorieLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalorieLogEntry(calorieLogEntry: CalorieLog)

    @Delete()
    suspend fun deleteCalorieLog(calorieLog: CalorieLog)
}
