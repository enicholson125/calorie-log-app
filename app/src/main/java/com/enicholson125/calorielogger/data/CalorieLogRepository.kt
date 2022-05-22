package com.enicholson125.calorielogger.data

import java.util.*


class CalorieLogRepository private constructor(
    private val calorieLogDAO: CalorieLogDAO
) {
    fun getCalorieTotal() = calorieLogDAO.getCalorieTotal()

    fun getSweetCalorieTotal() = calorieLogDAO.getSweetCalorieTotal()

    fun getDayCalorieTotal(date: Date) = calorieLogDAO.getDayCalorieTotal(date)

    fun getDaySweetCalorieTotal(date: Date) = calorieLogDAO.getDaySweetCalorieTotal(date)

    fun getLatestDailyBudgetTime() = calorieLogDAO.getLatestDailyBudgetTime()

    fun getLatestSweetDailyBudgetTime() = calorieLogDAO.getLatestSweetDailyBudgetTime()

    fun getCalorieLogsForDate(date: Date) = calorieLogDAO.getCalorieLogsForDate(date)

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
