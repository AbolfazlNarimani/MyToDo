package com.abe.todolist.fragments

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.abe.todolist.R
import com.abe.todolist.data.models.Priority
import com.abe.todolist.interfacepack.DateSelected
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar

class SharedViewModel(application: Application) : AndroidViewModel(application) , DateSelected {

    private var _datePickerLiveData : MutableLiveData<String> = MutableLiveData<String>()
    val datePickerLiveData = _datePickerLiveData


    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calender = GregorianCalendar()
        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.YEAR, year)
        val viewFormatter = SimpleDateFormat("dd-MM-YYYY")
        val viewFormattedDate = viewFormatter.format(calender.time)
        _datePickerLiveData.postValue(viewFormattedDate)
    }

    fun observeDateReceiver():MutableLiveData<String> {
        return datePickerLiveData
    }

    /** ============================= List Fragment ============================= */


    /** ============================= Add/Update Fragment ============================= */

    val listener: AdapterView.OnItemSelectedListener = object :

        AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(
            parent: AdapterView<*>?, view: View?, position: Int, id: Long
        ) {
            when (position) {
                0 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application, R.color.red
                        )
                    )
                }

                1 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application, R.color.yellow
                        )
                    )
                }

                2 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application, R.color.green
                        )
                    )
                }
            }
        }
    }

    fun verifyDataFromUser(title: String, description: String, userDate: String, userTime: String): Boolean {
        return !(title.isEmpty() || description.isEmpty() || userDate.isEmpty() || userTime.isEmpty())
    }

    fun parsePriority(priority: String): Priority {
        return when (priority) {
            "High Priority" -> {
                Priority.HIGH
            }

            "Medium Priority" -> {
                Priority.MEDIUM
            }

            "Low Priority" -> {
                Priority.LOW
            }

            else -> Priority.LOW
        }
    }



}