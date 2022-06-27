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
    private var overallBudgetEnabled = true
    private var sweetBudgetEnabled = true
    private var dailyBudgetAmount = 2000
    private var dailySweetBudgetAmount = 500
    private val latestDailyBudgetLog = calorieLogRepository.getLatestDailyBudgetLog(sweet = false)
    private val latestSweetDailyBudgetLog = calorieLogRepository.getLatestDailyBudgetLog(sweet = true)

    private fun updateBudgetAmount(amount: String?, sweet: Boolean) {
        if (amount != null && amount != "") {
            var calorieAmount = amount.toInt()
            if (sweet) {
                if (!sweetBudgetEnabled) {
                    calorieAmount = 0
                }
                dailySweetBudgetAmount = calorieAmount
            } else {
                if (sweetBudgetEnabled) {
                    calorieAmount = calorieAmount - dailySweetBudgetAmount
                }
                dailyBudgetAmount = calorieAmount
            }
            updateTodaysDailyBudgetCalories(calorieAmount, sweet)
        }
    }

    fun setValuesFromPreferences(sweetEnabled: Boolean, overallEnabled: Boolean, sweetAmount: String?, overallAmount: String?) {
        sweetBudgetEnabled = sweetEnabled
        overallBudgetEnabled = overallEnabled
        updateBudgetAmount(sweetAmount, sweet = true)
        updateBudgetAmount(overallAmount, sweet = false)
    }

    val todaysCalories = calorieLogRepository.getDayCalorieTotal(DateUtils.getCurrentDate())
    val todaysSweetCalories = calorieLogRepository.getSweetDayCalorieTotal(DateUtils.getCurrentDate())

    val dailyBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestDailyBudgetLog, ::addDailyCalories)
    }

    val dailySweetBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestSweetDailyBudgetLog, ::addDailySweetCalories)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDailyCalories(latestBudgetLog: CalorieLog) {
        if (!overallBudgetEnabled) {
            return
        }
        else if (latestBudgetLog.logID == "initoverallbudgetid") {
            addCalorieLog(dailyBudgetAmount, "Daily Budget Full", DateUtils.getCurrentDate(), false)
        }
        else if (!DateUtils.isDateToday(latestBudgetLog.timeLogged)) {
            addCalorieLog(dailyBudgetAmount, "Daily Budget Full", DateUtils.addOneDay(latestBudgetLog.timeLogged), false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDailySweetCalories(latestSweetBudgetLog: CalorieLog) {
        if (!sweetBudgetEnabled) {
            return
        }
        else if (latestSweetBudgetLog.logID == "initsweetbudgetid") {
            addCalorieLog(dailySweetBudgetAmount, "Daily Budget", DateUtils.getCurrentDate(), true)
        }
        else if (!DateUtils.isDateToday(latestSweetBudgetLog.timeLogged)) {
            addCalorieLog(dailySweetBudgetAmount, "Daily Budget", DateUtils.addOneDay(latestSweetBudgetLog.timeLogged), true)
        }
    }

    fun addUserCalorieLog(calories: Int, description: String, isSweet: Boolean) {
        addCalorieLog(-calories, description, DateUtils.getCurrentDateTime(), isSweet)
    }

    private fun addCalorieLog(calories: Int, description: String, timeLog: Date, isSweet: Boolean) {
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

    fun resetCalorieLogs() {
        viewModelScope.launch{
            calorieLogRepository.resetCalorieLogs()
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

    val sweetCalorieTotal: LiveData<Int> = calorieLogRepository.getSweetCalorieTotal()
    val calorieTotal: LiveData<Int> = calorieLogRepository.getCalorieTotal()

    val todaysUserCalorieLogs: LiveData<List<CalorieLog>> = calorieLogRepository.getUserCalorieLogsSinceDate(DateUtils.getCurrentDate())
    val allCalorieLogs: LiveData<List<CalorieLog>> = calorieLogRepository.getAllCalorieLogs()
}

