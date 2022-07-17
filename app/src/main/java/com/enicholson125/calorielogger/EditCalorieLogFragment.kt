package com.enicholson125.calorielogger

import android.app.AlertDialog
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.content.DialogInterface
import android.widget.EditText
import android.content.Context
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProvider
import com.enicholson125.calorielogger.utilities.InjectorUtils
import com.enicholson125.calorielogger.data.CalorieLog
import com.enicholson125.calorielogger.viewmodels.CalorieLogViewModel

class EditCalorieLogFragment(
    private val calorieLog: CalorieLog
): DialogFragment() {
    private lateinit var model: CalorieLogViewModel

    // Override the Fragment.onAttach() method to instantiate the AdoptionDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)

        model = ViewModelProvider(
            requireActivity(), InjectorUtils.provideCalorieLogViewModelFactory(requireActivity())
        ).get(CalorieLogViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.edit_calorie_log_fragment, null)

            val calorieEntry = view.findViewById<EditText>(R.id.edit_calories)
            calorieEntry.setText(calorieLog.getInvertedCalories().toString())

            val descriptionEntry = view.findViewById<EditText>(R.id.edit_description)
            descriptionEntry.setText(calorieLog.description)

            val sweetEntry = view.findViewById<CheckBox>(R.id.sweet_checkbox)
            sweetEntry.isChecked = calorieLog.isSweet

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                .setTitle(R.string.edit_logs_heading)
                // Add action buttons
                .setPositiveButton(R.string.update,
                    DialogInterface.OnClickListener { _, _ ->
                        model.updateCalorieLog(
                            calorieEntry.text.toString().toInt(),
                            descriptionEntry.text.toString(),
                            calorieLog.timeLogged,
                            sweetEntry.isChecked,
                            calorieLog.logID
                        )
                    })
                .setNeutralButton(R.string.delete,
                    DialogInterface.OnClickListener { _, _ ->
                        model.deleteCalorieLog(calorieLog)
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}