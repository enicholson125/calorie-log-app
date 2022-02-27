package com.enicholson125.meteor

import android.util.Log
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import android.widget.TextView
import com.enicholson125.meteor.utilities.InjectorUtils
import com.enicholson125.meteor.data.CalorieLog
import com.enicholson125.meteor.utilities.DateUtils
import com.enicholson125.meteor.viewmodels.CalorieLogViewModel
import java.util.*

class CalorieLogActivity : AppCompatActivity() {
    private val model: CalorieLogViewModel by viewModels {
        InjectorUtils.provideCalorieLogViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_page)

        val calorieCountView = findViewById<TextView>(R.id.calorie_count)
        val calorieCountObserver = Observer<Int> { calorieCount ->
            calorieCountView.text = calorieCount.toString()
        }
        model.calorieTotal.observe(this, calorieCountObserver)

        val calorieEntry = findViewById<EditText>(R.id.enter_calories)
        val descriptionEntry = findViewById<EditText>(R.id.enter_description)
        findViewById<Button>(R.id.add_calorie_log).setOnClickListener { _ ->
            model.addUserCalorieLog(
                calorieEntry.getText().toString().toInt(),
                descriptionEntry.getText().toString()
            )
        }

        val calorieLogView = findViewById<TextView>(R.id.calorie_logs)
        val calorieLogObserver = Observer<List<CalorieLog>> { calorieLogs ->
            calorieLogView.text = formatCalorieLogs(calorieLogs)
        }
        model.calorieLogs.observe(this, calorieLogObserver)

        val dailyBudgetTesterObserver = Observer<Date> {}
        model.dailyBudgetTester.observe(this, dailyBudgetTesterObserver)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { _ ->
            this.finish()
        }
    }

    fun formatCalorieLogs(logs: List<CalorieLog>): String {
        var formattedLogs = ""
        for (log in logs) {
            formattedLogs = formattedLogs.plus("${log.format()}\n")
        }
        return formattedLogs
    }
}
