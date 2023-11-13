package com.abe.todolist.fragments.add

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.abe.todolist.R
import com.abe.todolist.TimePickerFragment

import com.abe.todolist.data.models.ToDoData

import com.abe.todolist.data.viewmodel.ToDoViewModel
import com.abe.todolist.databinding.FragmentAddBinding
import com.abe.todolist.fragments.DatePickerFragment
import com.abe.todolist.notifications.CHANNELID
import com.abe.todolist.notifications.MESSEAGEEXTRA
import com.abe.todolist.notifications.NOTIFICATIONID
import com.abe.todolist.notifications.Notifications
import com.abe.todolist.notifications.TITLEEXTRA
import com.abe.todolist.fragments.SharedViewModel


class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var userDate: String = ""
    private var userTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMenu()

        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.onItemSelectedListener
        // date picker
        binding.dateBtn.setOnClickListener {
            showDatePicker()
        }
        binding.timeBtn.setOnClickListener {
            showTimePicker()
        }
        createNotificationChannel()
    }

    private fun setUpMenu() {
        // implement menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                if (menuItem.itemId == R.id.menu_add) {
                    insertDataToDB()
                    scheduleNotification()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun scheduleNotification() {
        val intent = Intent(requireContext().applicationContext, Notifications::class.java)
        val title = binding.titleEt.text.toString()
        val message = binding.descriptionEt.text.toString()
        intent.putExtra(TITLEEXTRA, title)
        intent.putExtra(MESSEAGEEXTRA, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext().applicationContext,
            NOTIFICATIONID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = mSharedViewModel.getTime()
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }



    private fun createNotificationChannel() {
        val name = "Notify Channel"
        val description = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNELID, name, importance)
        channel.description = description
        val notificationManager =
            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    // showing time picker and date picker to user
    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment(mSharedViewModel)
        datePickerFragment.show(parentFragmentManager, "datePicker")
        setDate()
    }

    private fun showTimePicker() {
        val timePickerFragment = TimePickerFragment(mSharedViewModel)
        timePickerFragment.show(parentFragmentManager, "timePicker")
        setTime()
    }

    // set time picker and date picker values
    private fun setTime() {
        mSharedViewModel.observeTimeReceiver().observe(viewLifecycleOwner) { time ->

            binding.selectedTime.text = time
            userTime = time
            binding.selectedTime.visibility = View.VISIBLE

        }
    }

    private fun setDate() {
        mSharedViewModel.observeDateReceiver().observe(viewLifecycleOwner) { date ->

            binding.selectedDate.text = date
            userDate = date
            binding.selectedDate.visibility = View.VISIBLE

        }

    }

    private fun insertDataToDB() {
        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        if (mSharedViewModel.verifyDataFromUser(mTitle, mDescription, userDate, userTime)) {
            // insert data to db

            val newData = ToDoData(
                id = 0,
                title = mTitle,
                priority = mSharedViewModel.parsePriority(mPriority),
                description = mDescription,
                userDate,
                userTime
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)

        } else Toast.makeText(
            requireContext(),
            "fill all the fields (Date and time included)",
            Toast.LENGTH_SHORT
        ).show()
    }

    // this is the fun that will be invoked after user picked a date


}
