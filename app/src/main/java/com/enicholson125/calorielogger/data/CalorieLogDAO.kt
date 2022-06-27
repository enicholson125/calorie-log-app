package com.enicholson125.calorielogger.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

/**
 * The Data Access Object for the [CalorieLog] class.
 */
@Dao
interface CalorieLogDAO {
    @Query("SELECT SUM(calories) FROM calorie_log WHERE sweet=1")
    fun getSweetCalorieTotal(): LiveData<Int>

    @Query("SELECT SUM(calories) FROM calorie_log")
    fun getCalorieTotal(): LiveData<Int>

    @Query("SELECT SUM(calories) FROM calorie_log WHERE time_logged >= :date AND description NOT LIKE 'Daily Budget%'")
    fun getDayCalorieTotal(date: Date): LiveData<Int>

    @Query("SELECT SUM(calories) FROM calorie_log WHERE sweet=1 AND time_logged >= :date AND description NOT LIKE 'Daily Budget%'")
    fun getSweetDayCalorieTotal(date: Date): LiveData<Int>

    @Query("SELECT id,time_logged,calories,description,sweet FROM calorie_log ORDER BY time_logged DESC")
    fun getAllCalorieLogs(): LiveData<List<CalorieLog>>

    @Query("SELECT id,time_logged,calories,description,sweet FROM calorie_log WHERE description NOT LIKE 'Daily Budget%' AND time_logged >= :date ORDER BY time_logged DESC")
    fun getUserCalorieLogsSinceDate(date: Date): LiveData<List<CalorieLog>>

    @Query("SELECT id,time_logged,calories,description,sweet FROM calorie_log WHERE description LIKE 'Daily Budget%' AND sweet=:sweet ORDER BY time_logged DESC LIMIT 1")
    fun getLatestDailyBudgetLog(sweet: Boolean): LiveData<CalorieLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalorieLogEntry(calorieLogEntry: CalorieLog)

    @Delete()
    suspend fun deleteCalorieLog(calorieLog: CalorieLog)

    @Query("DELETE FROM calorie_log WHERE id != 'initsweetbudgetid' AND id != 'initoverallbudgetid'")
    suspend fun resetCalorieLogs()
}
