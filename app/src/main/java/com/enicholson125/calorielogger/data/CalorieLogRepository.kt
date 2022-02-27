package com.enicholson125.calorielogger.data


class CalorieLogRepository private constructor(
    private val calorieLogDAO: CalorieLogDAO
) {
    fun getCalorieTotal() = calorieLogDAO.getCalorieTotal()

    fun getLatestDailyBudgetTime() = calorieLogDAO.getLatestDailyBudgetTime()

    fun getLatestTenCalorieLogs() = calorieLogDAO.getLatestTenCalorieLogs()

    suspend fun insertCalorieLogEntry(calorieLogEntry: CalorieLog) = calorieLogDAO.insertCalorieLogEntry(calorieLogEntry)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: CalorieLogRepository? = null

        fun getInstance(calorieLogDAO: CalorieLogDAO) =
            instance ?: synchronized(this) {
                instance ?: CalorieLogRepository(calorieLogDAO).also { instance = it }
            }
    }
}
