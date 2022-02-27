package com.enicholson125.meteor.utilities

import android.content.Context
import com.enicholson125.meteor.data.*
import com.enicholson125.meteor.viewmodels.CalorieLogViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getCalorieLogRepository(context: Context): CalorieLogRepository {
        return CalorieLogRepository.getInstance(
            AppDatabase.getInstance(context).calorieLogDAO()
        )
    }

    fun provideCalorieLogViewModelFactory(
        context: Context,
    ): CalorieLogViewModelFactory {
        return CalorieLogViewModelFactory(
            getCalorieLogRepository(context),
        )
    }
}
