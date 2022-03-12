package com.enicholson125.calorielogger

import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import android.widget.TextView
import com.enicholson125.calorielogger.utilities.InjectorUtils
import com.enicholson125.calorielogger.data.CalorieLog
import com.enicholson125.calorielogger.viewmodels.CalorieLogViewModel
import kotlinx.android.synthetic.main.main_page.*
import java.util.*

class CalorieLogActivity : AppCompatActivity() {
    private val model: CalorieLogViewModel by viewModels {
        InjectorUtils.provideCalorieLogViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_page)

        val sweetCalorieCountView = findViewById<TextView>(R.id.sweet_calorie_count)
        val sweetCalorieCountObserver = Observer<Int> { calorieCount ->
            animateNumber(
                getCountAsInt(sweetCalorieCountView.text.toString()),
                calorieCount,
                sweetCalorieCountView
            )
        }
        model.sweetCalorieTotal.observe(this, sweetCalorieCountObserver)

        val calorieCountView = findViewById<TextView>(R.id.calorie_count)
        val calorieCountObserver = Observer<Int> { calorieCount ->
            animateNumber(
                getCountAsInt(calorieCountView.text.toString()),
                calorieCount,
                calorieCountView
            )
        }
        model.calorieTotal.observe(this, calorieCountObserver)

        val checkBox = findViewById<CheckBox>(R.id.sweet_checkbox);

        val calorieEntry = findViewById<EditText>(R.id.enter_calories)
        val descriptionEntry = findViewById<EditText>(R.id.enter_description)
        findViewById<Button>(R.id.add_calorie_log).setOnClickListener (fun(_) {
            model.addUserCalorieLog(
                calorieEntry.getText().toString().toInt(),
                descriptionEntry.getText().toString(),
                checkBox.isChecked()
            )
            calorieEntry.setText("")
            descriptionEntry.setText("")
            checkBox.setChecked(false)
        })

        val calorieLogView = findViewById<TextView>(R.id.calorie_logs)
        val calorieLogObserver = Observer<List<CalorieLog>> { calorieLogs ->
            calorieLogView.text = formatCalorieLogs(calorieLogs)
        }
        model.calorieLogs.observe(this, calorieLogObserver)

        val dailyBudgetTesterObserver = Observer<Date> {}
        model.dailyBudgetTester.observe(this, dailyBudgetTesterObserver)

        val dailySweetBudgetTesterObserver = Observer<Date> {}
        model.dailySweetBudgetTester.observe(this, dailySweetBudgetTesterObserver)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { _ ->
            this.finish()
        }
    }

    private fun getCountAsInt(count: String): Int {
        if (count == getString(R.string.unset)) {
            return 0
        } else {
            return count.toInt()
        }
    }

    private fun animateNumber(from: Int, to: Int, view: TextView) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            view.text = animation.animatedValue.toString()
        }
        animator.start()
    }

    fun formatCalorieLogs(logs: List<CalorieLog>): String {
        var formattedLogs = ""
        for (log in logs) {
            formattedLogs = formattedLogs.plus("${log.format()}\n")
        }
        return formattedLogs
    }
}
