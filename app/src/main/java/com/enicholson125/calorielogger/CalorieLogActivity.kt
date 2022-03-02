package com.enicholson125.calorielogger

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import android.widget.TextView
import com.enicholson125.calorielogger.utilities.InjectorUtils
import com.enicholson125.calorielogger.data.CalorieLog
import com.enicholson125.calorielogger.viewmodels.CalorieLogViewModel
import java.util.*
import nl.dionsegijn.konfetti.xml.KonfettiView
import com.enicholson125.calorielogger.konfetticonfig.Presets

class CalorieLogActivity : AppCompatActivity() {
    private val model: CalorieLogViewModel by viewModels {
        InjectorUtils.provideCalorieLogViewModelFactory(this)
    }

    private lateinit var viewKonfetti: KonfettiView

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

        viewKonfetti = findViewById(R.id.konfettiView)
        findViewById<Button>(R.id.btnFestive).setOnClickListener { festive() }
        findViewById<Button>(R.id.btnExplode).setOnClickListener { explode() }
        findViewById<Button>(R.id.btnParade).setOnClickListener { parade() }
        findViewById<Button>(R.id.btnRain).setOnClickListener { rain() }
    }


    private fun festive() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.festive())
    }

    private fun explode() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.explode())
    }

    private fun parade() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.parade())
    }

    private fun rain() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.rain())
    }

    fun formatCalorieLogs(logs: List<CalorieLog>): String {
        var formattedLogs = ""
        for (log in logs) {
            formattedLogs = formattedLogs.plus("${log.format()}\n")
        }
        return formattedLogs
    }
}
