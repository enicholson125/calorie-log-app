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
    private val latestDailyBudgetLog = calorieLogRepository.getLatestDailyBudgetLog(sweet = false)
    private val latestSweetDailyBudgetLog = calorieLogRepository.getLatestDailyBudgetLog(sweet = true)

    fun setDailyBudgetAmount(amount: String?) {
        if (amount != null && amount != "") {
            val calorieAmount = amount.toInt()
            dailyBudgetAmount = calorieAmount
            updateTodaysDailyBudgetCalories(calorieAmount, sweet = false)
        }
    }

    fun setDailySweetBudgetAmount(amount: String?) {
        if (amount != null && amount != "") {
            val calorieAmount = amount.toInt()
            dailyBudgetAmount = calorieAmount
            updateTodaysDailyBudgetCalories(calorieAmount, sweet = true)
        }
    }

    val todaysCalories = calorieLogRepository.getDayCalorieTotal(DateUtils.getCurrentDate(), sweet = false)
    val todaysSweetCalories = calorieLogRepository.getDayCalorieTotal(DateUtils.getCurrentDate(), sweet = true)

    val dailyBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestDailyBudgetLog, ::addDailyCalories)
    }

    val dailySweetBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestSweetDailyBudgetLog, ::addDailySweetCalories)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDailyCalories(latestBudgetLog: CalorieLog) {
        if (latestBudgetLog.logID == "initoverallbudgetid") {
            addCalorieLog(dailyBudgetAmount, "Daily Budget Full", DateUtils.getCurrentDate(), false)
        }
        else if (!DateUtils.isDateToday(latestBudgetLog.timeLogged) && overallBudgetEnabled) {
            addCalorieLog(dailyBudgetAmount, "Daily Budget Full", DateUtils.addOneDay(latestBudgetLog.timeLogged), false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDailySweetCalories(latestSweetBudgetLog: CalorieLog) {
        if (latestSweetBudgetLog.logID == "initsweetbudgetid") {
            addCalorieLog(dailySweetBudgetAmount, "Daily Budget", DateUtils.getCurrentDate(), true)
        }
        else if (!DateUtils.isDateToday(latestSweetBudgetLog.timeLogged) && sweetBudgetEnabled) {
            addCalorieLog(dailySweetBudgetAmount, "Daily Budget", DateUtils.addOneDay(latestSweetBudgetLog.timeLogged), true)
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

    private fun updateTodaysDailyBudgetCalories(calories: Int, sweet: Boolean) {
        val log: CalorieLog?
        if (sweet) {
            log = latestSweetDailyBudgetLog.value
        } else {
            log = latestDailyBudgetLog.value
        }
        if (log != null) {
            updateCalorieLog(-calories, log.description, log.timeLogged, log.isSweet, log.logID)
        }
    }

    val sweetCalorieTotal: LiveData<Int> = calorieLogRepository.getCalorieTotal(sweet = true)
    val calorieTotal: LiveData<Int> = calorieLogRepository.getCalorieTotal(sweet = false)

    val calorieLogs: LiveData<List<CalorieLog>> = calorieLogRepository.getCalorieLogsSinceDate(DateUtils.getCurrentDate())
}

