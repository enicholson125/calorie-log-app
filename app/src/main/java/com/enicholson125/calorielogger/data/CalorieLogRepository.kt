package com.enicholson125.calorielogger.data

import java.util.*


class CalorieLogRepository private constructor(
    private val calorieLogDAO: CalorieLogDAO
) {
    fun getCalorieTotal( sweet: Boolean) = calorieLogDAO.getCalorieTotal(sweet)

    fun getDayCalorieTotal(date: Date, sweet: Boolean) = calorieLogDAO.getDayCalorieTotal(date, sweet)

    fun getLatestDailyBudgetLog(sweet: Boolean) = calorieLogDAO.getLatestDailyBudgetLog(sweet)

    fun getCalorieLogsSinceDate(date: Date) = calorieLogDAO.getCalorieLogsSinceDate(date)

    suspend fun insertCalorieLogEntry(calorieLogEntry: CalorieLog) = calorieLogDAO.insertCalorieLogEntry(calorieLogEntry)

    suspend fun deleteCalorieLog(log: CalorieLog) = calorieLogDAO.deleteCalorieLog(log)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: CalorieLogRepository? = null

        fun getInstance(calorieLogDAO: CalorieLogDAO) =
            instance ?: synchronized(this) {
                instance ?: CalorieLogRepository(calorieLogDAO).also { instance = it }
            }
    }
}
