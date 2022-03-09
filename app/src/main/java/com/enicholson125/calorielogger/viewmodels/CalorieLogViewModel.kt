package com.enicholson125.calorielogger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.enicholson125.calorielogger.data.*
import com.enicholson125.calorielogger.utilities.GeneratorUtils
import com.enicholson125.calorielogger.utilities.DateUtils
import kotlinx.coroutines.launch
import java.util.*

/**
 * The ViewModel used in [CalorieLogActivity].
 */
class CalorieLogViewModel(
    private val calorieLogRepository: CalorieLogRepository,
) : ViewModel() {
    private val idLength = 10
    private val dailyBudgetAmount = 2500
    private val dailySweetBudgetAmount = 500
    private val latestDailyBudgetTime = calorieLogRepository.getLatestDailyBudgetTime()
    private val latestSweetDailyBudgetTime = calorieLogRepository.getLatestSweetDailyBudgetTime()

    val dailyBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestDailyBudgetTime, ::addDailyCalories)
    }

    val dailySweetBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestSweetDailyBudgetTime, ::addDailySweetCalories)
    }

    fun addDailyCalories(latestBudgetTime: Date) {
        if (!DateUtils.isDateToday(latestBudgetTime)) {
            addCalorieLog(dailyBudgetAmount, "Daily Budget Full", DateUtils.addOneDay(latestBudgetTime), false)
        }
    }

    fun addDailySweetCalories(latestSweetBudgetTime: Date) {
        if (!DateUtils.isDateToday(latestSweetBudgetTime)) {
            addCalorieLog(dailySweetBudgetAmount, "Daily Budget", DateUtils.addOneDay(latestSweetBudgetTime), true)
        }
    }

    fun addUserCalorieLog(calories: Int, description: String, isSweet: Boolean) {
        addCalorieLog(-calories, description, DateUtils.getCurrentDateTime(), isSweet)
    }

    fun addCalorieLog(calories: Int, description: String, timeLog: Date, isSweet: Boolean) {
        viewModelScope.launch{
            calorieLogRepository.insertCalorieLogEntry(
                CalorieLog(GeneratorUtils.getRandomID(idLength), timeLog, calories, description, isSweet)
            )
        }
    }

    val sweetCalorieTotal: LiveData<Int> = calorieLogRepository.getSweetCalorieTotal()
    val calorieTotal: LiveData<Int> = calorieLogRepository.getCalorieTotal()

    val calorieLogs: LiveData<List<CalorieLog>> = calorieLogRepository.getLatestTenCalorieLogs()
}

