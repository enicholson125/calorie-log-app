package com.enicholson125.calorielogger.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
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
    var overallBudgetEnabled = true
    var sweetBudgetEnabled = true
    private var dailyBudgetAmount = 2000
    private var dailySweetBudgetAmount = 500
    private val latestDailyBudgetTime = calorieLogRepository.getLatestDailyBudgetTime()
    private val latestSweetDailyBudgetTime = calorieLogRepository.getLatestSweetDailyBudgetTime()

    fun setDailyBudgetAmount(amount: String?) {
        if (amount != null && amount != "") {
            dailyBudgetAmount = amount.toInt()
        }
    }

    fun setDailySweetBudgetAmount(amount: String?) {
        if (amount != null && amount != "") {
            dailySweetBudgetAmount = amount.toInt()
        }
    }

    val todaysCalories = calorieLogRepository.getDayCalorieTotal(DateUtils.getCurrentDate())
    val todaysSweetCalories = calorieLogRepository.getDaySweetCalorieTotal(DateUtils.getCurrentDate())

    val dailyBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestDailyBudgetTime, ::addDailyCalories)
    }

    val dailySweetBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestSweetDailyBudgetTime, ::addDailySweetCalories)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDailyCalories(latestBudgetTime: Date) {
        if (!DateUtils.isDateToday(latestBudgetTime) && overallBudgetEnabled) {
            addCalorieLog(dailyBudgetAmount, "Daily Budget Full", DateUtils.addOneDay(latestBudgetTime), false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDailySweetCalories(latestSweetBudgetTime: Date) {
        if (!DateUtils.isDateToday(latestSweetBudgetTime) && sweetBudgetEnabled) {
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

    fun updateCalorieLog(calories: Int, description: String, timeLog: Date, isSweet: Boolean, id: String) {
        viewModelScope.launch{
            calorieLogRepository.insertCalorieLogEntry(CalorieLog(id, timeLog, -calories, description, isSweet))
        }
    }

    fun deleteCalorieLog(log: CalorieLog) {
        viewModelScope.launch{
            calorieLogRepository.deleteCalorieLog(log)
        }
    }

    val sweetCalorieTotal: LiveData<Int> = calorieLogRepository.getSweetCalorieTotal()
    val calorieTotal: LiveData<Int> = calorieLogRepository.getCalorieTotal()

    val calorieLogs: LiveData<List<CalorieLog>> = calorieLogRepository.getCalorieLogsForDate(DateUtils.getCurrentDate())
}

