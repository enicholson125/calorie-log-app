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

    private fun getCountAsInt(count: String): Int {
        if (count == getString(R.string.unset)) {
            return 0
        } else {
            return count.toInt()
        }
    }


    fun showEditLogDialog(log: CalorieLog) {
        val editLogDialog = EditCalorieLogFragment(log)
        editLogDialog.show(supportFragmentManager, "edit")
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
