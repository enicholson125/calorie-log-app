package com.enicholson125.calorielogger

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
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
                calorieEntry.text.toString().toInt(),
                descriptionEntry.text.toString(),
                checkBox.isChecked
            )
            calorieEntry.setText("")
            descriptionEntry.setText("")
            checkBox.isChecked = false
        })

        val calorieLogTable = findViewById<TableLayout>(R.id.calorie_logs)
        val calorieLogObserver = Observer<List<CalorieLog>> { calorieLogs ->
            calorieLogTable.removeAllViews()
            for (log in calorieLogs) {
                val row = TableRow(this)
                row.id = View.generateViewId()
                row.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val calories = TextView(this)
                calories.id = View.generateViewId()
                calories.text = log.calories.toString()
                calories.setPadding(30, 10, 30, 10)
                row.addView(calories)
                val description = TextView(this)
                description.id = View.generateViewId()
                description.text = log.description
                description.setPadding(30, 10, 30, 10)
                row.addView(description)
                val time = TextView(this)
                time.id = View.generateViewId()
                time.text = log.timeLogged.toString()
                time.setPadding(30, 10, 30, 10)
                row.addView(time)
                calorieLogTable.addView(row)
            }
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
}
