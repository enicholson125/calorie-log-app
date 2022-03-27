package com.enicholson125.calorielogger

import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.enicholson125.calorielogger.utilities.InjectorUtils
import com.enicholson125.calorielogger.data.CalorieLog
import com.enicholson125.calorielogger.viewmodels.CalorieLogViewModel
import java.util.*

class CalorieLogActivity : AppCompatActivity() {
    private val model: CalorieLogViewModel by viewModels {
        InjectorUtils.provideCalorieLogViewModelFactory(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_page)

        configureCalorieCount(
            R.id.sweet_calorie_count,
            R.id.daily_sweet_calorie_count,
            model.sweetCalorieTotal,
            model.todaysSweetCalories
        )

        configureCalorieCount(
            R.id.calorie_count,
            R.id.daily_calorie_count,
            model.calorieTotal,
            model.todaysCalories
        )

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
        calorieLogTable.setPadding(40, 60, 40, 0)
        val calorieLogObserver = Observer<List<CalorieLog>> { calorieLogs ->
            calorieLogTable.removeAllViews()
            for (log in calorieLogs) {
                if ((log.description != "Daily Budget") && (log.description != "Daily Budget Full")) {
                    val row = TableRow(this)
                    row.id = View.generateViewId()
                    row.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    row.addView(createLogTableView(log.getInvertedCalories().toString()))
                    row.addView(createLogTableView(log.description))
                    row.addView(createEditLogButton(log))
                    row.setPadding(0, 10, 0, 20)
                    calorieLogTable.addView(row)
                }
            }
        }
        model.calorieLogs.observe(this, calorieLogObserver)

        val dailyBudgetTesterObserver = Observer<Date> {}
        model.dailyBudgetTester.observe(this, dailyBudgetTesterObserver)

        val dailySweetBudgetTesterObserver = Observer<Date> {}
        model.dailySweetBudgetTester.observe(this, dailySweetBudgetTesterObserver)
    }

    private fun createLogTableView(text: String): TextView {
        val view = TextView(this)
        view.id = View.generateViewId()
        view.text = text
        view.gravity = Gravity.CENTER
        view.setPadding(10, 10, 10, 10)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createEditLogButton(log: CalorieLog): ImageButton {
        val button = ImageButton(this)
        button.setOnClickListener{ _ ->
            showEditLogDialog(log)
        }
        button.setImageResource(R.drawable.ic_baseline_edit_24)
        button.setBackgroundResource(R.color.colorPrimaryDark)
        button.setPadding(10, 10, 10, 10)
        button.foregroundGravity = Gravity.CENTER
        return button
    }

    private fun toggleVisibility(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        } else if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        }
    }

    fun showEditLogDialog(log: CalorieLog) {
        val editLogDialog = EditCalorieLogFragment(log)
        editLogDialog.show(supportFragmentManager, "edit")
    }

    private fun animateNumberInView(to: Int, view: TextView) {
        val animator = ValueAnimator.ofInt(view.text.toString().toInt(), to)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            view.text = animation.animatedValue.toString()
        }
        animator.start()
    }

    private fun animateTodaysCount(to: Int?, view: TextView) {
        if (to != null) {
            animateNumberInView(-to, view)
        }
    }

    private fun configureCalorieCount(
        totalViewID: Int,
        dailyViewID: Int,
        totalCount: LiveData<Int>,
        dailyCount: LiveData<Int>
    ) {
        val totalView = findViewById<TextView>(totalViewID)
        val totalCountObserver = Observer<Int> { calorieCount ->
            animateNumberInView(
                calorieCount,
                totalView
            )
        }
        totalCount.observe(this, totalCountObserver)

        val dailyView = findViewById<TextView>(dailyViewID)
        val dailyCountObserver = Observer<Int> { calorieCount ->
            animateTodaysCount(
                calorieCount,
                dailyView
            )
        }
        dailyCount.observe(this, dailyCountObserver)

        totalView.setOnClickListener{_ ->
            toggleVisibility(totalView)
            toggleVisibility(dailyView)
        }
        dailyView.setOnClickListener{_ ->
            toggleVisibility(totalView)
            toggleVisibility(dailyView)
        }
    }
}
