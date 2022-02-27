package com.enicholson125.calorielogger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enicholson125.calorielogger.data.CalorieLogRepository

/**
 * Factory for creating a [CalorieLogViewModel] with a constructor that
 * takes a [CalorieLogRepository]
 */
class CalorieLogViewModelFactory(
    private val calorieLogRepository: CalorieLogRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalorieLogViewModel(
            calorieLogRepository
        ) as T
    }
}
