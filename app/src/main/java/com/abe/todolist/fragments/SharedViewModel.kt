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
import com.abe.todolist.interfacepack.TimeSelected
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar

class SharedViewModel(application: Application) : AndroidViewModel(application), DateSelected,
    TimeSelected {

    private var _datePickerLiveData: MutableLiveData<String> = MutableLiveData<String>()
    private val _timePickerLiveData: MutableLiveData<String> = MutableLiveData<String>()


    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calender = GregorianCalendar()
        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.YEAR, year)
        val viewFormatter = SimpleDateFormat("dd-MM-YYYY")
        val viewFormattedDate = viewFormatter.format(calender.time)
        _datePickerLiveData.postValue(viewFormattedDate)

        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
    }

    override fun receiveTime(hour: Int, minute: Int) {
        /*userTime = "$hour : $minute"
        binding.selectedDate.text = userTime*/
        val calender = GregorianCalendar()
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, minute)
        val viewFormat = SimpleDateFormat("mm:hh")
        val viewFormattedTime = viewFormat.format(calender.time)
        _timePickerLiveData.postValue(viewFormattedTime)

        this.hour = hour
        this.minute = minute
    }

    fun observeDateReceiver(): MutableLiveData<String> {
        return _datePickerLiveData
    }
    fun observeTimeReceiver(): MutableLiveData<String>{
        return _timePickerLiveData
    }

    private var hour = 0
    private var minute = 0
    private var dayOfMonth = 0
    private var month = 0
    private var year = 0
     fun getTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(/* year = */ this.year,/* month = */
            this.month,/* date = */
            this.dayOfMonth,/* hourOfDay = */
            this.hour,/* minute = */
            (this.minute - 1)
        )
        return calendar.timeInMillis
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

    fun verifyDataFromUser(
        title: String,
        description: String,
        userDate: String,
        userTime: String
    ): Boolean {
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