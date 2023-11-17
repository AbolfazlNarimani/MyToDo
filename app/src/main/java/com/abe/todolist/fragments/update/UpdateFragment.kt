package com.abe.todolist.fragments.update

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abe.todolist.R
import com.abe.todolist.fragments.DateAndTime.TimePickerFragment
import com.abe.todolist.data.models.ToDoData
import com.abe.todolist.data.viewmodel.ToDoViewModel
import com.abe.todolist.databinding.FragmentUpdateBinding
import com.abe.todolist.fragments.DateAndTime.DatePickerFragment
import com.abe.todolist.fragments.SharedViewModel
import com.abe.todolist.notifications.CHANNELID
import com.abe.todolist.notifications.MESSEAGEEXTRA
import com.abe.todolist.notifications.NOTIFICATIONID
import com.abe.todolist.notifications.Notifications
import com.abe.todolist.notifications.TITLEEXTRA


class UpdateFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBinding
    private val args by navArgs<UpdateFragmentArgs>()

    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mTodoViewModel: ToDoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.args = this.args
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMenu()
        populateFields()
        binding.timeBtn.setOnClickListener {
            showTimePicker()
        }
        binding.dateBtn.setOnClickListener {
            showDatePicker()
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Notify Channel"
        val description = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNELID, name, importance)
        channel.description = description
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification() {
        val intent = Intent(requireContext().applicationContext, Notifications::class.java)
        val title = binding.currentTitleEt.text.toString()
        val message = binding.currentDescriptionEt.text.toString()
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

    private fun populateFields() {
        /*binding.apply {
            currentTitleEt.setText(args.currentitem.title)
            currentDescriptionEt.setText(args.currentitem.description)
            currentPrioritiesSpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentitem.priority))

            tvTime.text = args.currentitem.time
            tvDate.text = args.currentitem.date
        }*/
        binding.currentPrioritiesSpinner.onItemSelectedListener =
                mSharedViewModel.onItemSelectedListener
    }

    private fun setUpMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                    R.id.menu_save -> updateItem()
                    R.id.menu_delete -> deleteItem()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun deleteItem() {
        confirmRemoval {
            if (it){
                mTodoViewModel.deleteItem(args.currentitem)
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                Toast.makeText(requireContext(),"Removed!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmRemoval(callback: (confirmed: Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            callback(true)
        }
        builder.setNegativeButton("No") { _, _ ->
            callback(false)
        }
        builder.setTitle("Delete '${args.currentitem.title}'?")
        builder.setMessage("Are you sure you want to remove '${args.currentitem.title}'?")
        builder.create().show()
    }

    private fun updateItem() {
        binding.apply {
            val title = currentTitleEt.text.toString()
            val description = currentDescriptionEt.text.toString()
            val priority = currentPrioritiesSpinner.selectedItem.toString()
            val time = tvTime.text.toString()
            val date = tvDate.text.toString()

            if (mSharedViewModel.verifyDataFromUser(title, description, date, time)) {
                val updateItem = args?.currentitem?.let {
                    ToDoData(
                        it.id,
                        title,
                        mSharedViewModel.parsePriority(priority),
                        description,
                        date,
                        time
                    )
                }
                if (updateItem != null) {
                    mTodoViewModel.updateData(updateItem)
                }
                scheduleNotification()
                Toast.makeText(
                    requireContext(),
                    "Updated!",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "fill all the fields (Date and time included)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

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
            binding.tvTime.text = time
        }
    }

    private fun setDate() {
        mSharedViewModel.observeDateReceiver().observe(viewLifecycleOwner) { date ->
            binding.tvDate.text = date
        }

    }

}