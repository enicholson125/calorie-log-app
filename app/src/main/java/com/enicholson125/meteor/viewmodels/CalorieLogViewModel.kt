package com.enicholson125.meteor.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Observer
import com.enicholson125.meteor.data.*
import com.enicholson125.meteor.utilities.GeneratorUtils
import com.enicholson125.meteor.utilities.DateUtils
import kotlinx.coroutines.launch
import java.util.*

/**
 * The ViewModel used in [CalorieLogActivity].
 */
class CalorieLogViewModel(
    private val calorieLogRepository: CalorieLogRepository,
) : ViewModel() {
    private val idLength = 10
    private val dailyBudgetAmount = 500
    val latestDailyBudgetTime = calorieLogRepository.getLatestDailyBudgetTime()

    val dailyBudgetTester = MediatorLiveData<Date>().apply {
        addSource(latestDailyBudgetTime, ::addDailyCalories)
    }

    fun addDailyCalories(latestBudgetTime: Date) {
        if (!DateUtils.isDateToday(latestBudgetTime)) {
            addCalorieLog(dailyBudgetAmount, "Daily Budget", DateUtils.addOneDay(latestBudgetTime))
        }
    }

    fun addUserCalorieLog(calories: Int, description: String) {
        addCalorieLog(-calories, description, DateUtils.getCurrentDateTime())
    }

    fun addCalorieLog(calories: Int, description: String, timeLog: Date) {
        viewModelScope.launch{
            calorieLogRepository.insertCalorieLogEntry(
                CalorieLog(GeneratorUtils.getRandomID(idLength), timeLog, calories, description)
            )
        }
    }

    val calorieTotal: LiveData<Int> = calorieLogRepository.getCalorieTotal()

    val calorieLogs: LiveData<List<CalorieLog>> = calorieLogRepository.getLatestTenCalorieLogs()
}

