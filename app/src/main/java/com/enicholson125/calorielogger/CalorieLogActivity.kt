package com.enicholson125.calorielogger

import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.enicholson125.calorielogger.utilities.InjectorUtils
import com.enicholson125.calorielogger.data.CalorieLog
import com.enicholson125.calorielogger.viewmodels.CalorieLogViewModel
import java.util.*


class CalorieLogActivity : AppCompatActivity() {
    private val model: CalorieLogViewModel by viewModels {
        InjectorUtils.provideCalorieLogViewModelFactory(this)
    }
    private var overallBudgetEnabled = true
    private var sweetBudgetEnabled = true

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_page)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        updateFromPreferences()

        configureCalorieCount(
            findViewById(R.id.sweet_calorie_bar),
            findViewById(R.id.sweet_calorie_count),
            findViewById(R.id.daily_sweet_calorie_count),
            findViewById(R.id.sweet_budget_left_title),
            findViewById(R.id.sweet_daily_calorie_title),
            model.sweetCalorieTotal,
            model.todaysSweetCalories
        )

        configureCalorieCount(
            findViewById(R.id.overall_calorie_bar),
            findViewById(R.id.calorie_count),
            findViewById(R.id.daily_calorie_count),
            findViewById(R.id.overall_budget_left_title),
            findViewById(R.id.overall_daily_calorie_title),
            model.calorieTotal,
            model.todaysCalories
        )

        val checkBox = findViewById<CheckBox>(R.id.sweet_checkbox)

        val calorieEntry = findViewById<EditText>(R.id.enter_calories)
        calorieEntry.addTextChangedListener(textWatcher)
        val descriptionEntry = findViewById<EditText>(R.id.enter_description)
        findViewById<Button>(R.id.add_calorie_log).setOnClickListener (fun(_) {
            if (calorieEntry.text.isNotEmpty()) {
                model.addUserCalorieLog(
                    calorieEntry.text.toString().toInt(),
                    descriptionEntry.text.toString(),
                    checkBox.isChecked
                )
            }
            calorieEntry.setText("")
            descriptionEntry.setText("")
            checkBox.isChecked = sweetBudgetEnabled && !overallBudgetEnabled
        })

        val calorieLogTable = findViewById<TableLayout>(R.id.calorie_logs)
        calorieLogTable.setPadding(40, 50, 40, 0)
        val calorieLogObserver = Observer<List<CalorieLog>> { calorieLogs ->
            calorieLogTable.removeAllViews()
            for (log in calorieLogs) {
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
        model.todaysUserCalorieLogs.observe(this, calorieLogObserver)

        val dailyBudgetTesterObserver = Observer<Date> {}
        model.dailyBudgetTester.observe(this, dailyBudgetTesterObserver)

        val dailySweetBudgetTesterObserver = Observer<Date> {}
        model.dailySweetBudgetTester.observe(this, dailySweetBudgetTesterObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        button.setOnClickListener{ showEditLogDialog(log) }
        button.setImageResource(R.drawable.ic_baseline_edit_24)
        button.setBackgroundResource(R.color.mid_yellow)
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

    private fun updateFromPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val sweetCheckBox = findViewById<CheckBox>(R.id.sweet_checkbox)

        sweetBudgetEnabled = sharedPreferences.getBoolean("sweet_budget_enabled", true)
        overallBudgetEnabled = sharedPreferences.getBoolean("overall_budget_enabled", true)
        val sweetBudgetAmount = sharedPreferences.getString("sweet_budget_quantity", "")
        val overallBudgetAmount = sharedPreferences.getString("overall_budget_quantity", "")

        model.setValuesFromPreferences(sweetBudgetEnabled, overallBudgetEnabled, sweetBudgetAmount, overallBudgetAmount)

        if (sweetBudgetEnabled) {
            findViewById<LinearLayout>(R.id.sweet_calorie_bar).visibility = View.VISIBLE
            sweetCheckBox.isChecked = true
        } else {
            findViewById<LinearLayout>(R.id.sweet_calorie_bar).visibility = View.GONE
            sweetCheckBox.visibility = View.GONE
        }

        if (overallBudgetEnabled) {
            findViewById<LinearLayout>(R.id.overall_calorie_bar).visibility = View.VISIBLE
            sweetCheckBox.isChecked = false
        } else {
            findViewById<LinearLayout>(R.id.overall_calorie_bar).visibility = View.GONE
            sweetCheckBox.visibility = View.GONE
        }

        if (sweetBudgetEnabled && overallBudgetEnabled) {
            // It will end up this way anyway, due to the ordering of the two clauses
            // but let's be explicit
            sweetCheckBox.isChecked = false
            sweetCheckBox.visibility = View.VISIBLE
        }

        if (sharedPreferences.getBoolean("reset", false)) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("reset", false)
            editor.apply()
            model.resetCalorieLogs()
        }
    }

    override fun onResume() {
        super.onResume()
        updateFromPreferences()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString() == "") {
                findViewById<Button>(R.id.add_calorie_log).isEnabled = false
            } else {
                findViewById<Button>(R.id.add_calorie_log).isEnabled = true
            }
        }
    }

    fun showSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
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
        countBar: LinearLayout,
        totalView: TextView,
        dailyView: TextView,
        totalTitle: TextView,
        dailyTitle: TextView,
        totalCount: LiveData<Int>,
        dailyCount: LiveData<Int>
    ) {
        val totalCountObserver = Observer<Int> { calorieCount ->
            animateNumberInView(
                calorieCount,
                totalView
            )
        }
        totalCount.observe(this, totalCountObserver)

        val dailyCountObserver = Observer<Int> { calorieCount ->
            animateTodaysCount(
                calorieCount,
                dailyView
            )
        }
        dailyCount.observe(this, dailyCountObserver)

        countBar.setOnClickListener{
            toggleVisibility(totalView)
            toggleVisibility(dailyView)
            toggleVisibility(totalTitle)
            toggleVisibility(dailyTitle)
        }
    }
}
