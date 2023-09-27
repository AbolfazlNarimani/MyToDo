package com.abe.todolist.fragments.add

import android.os.Bundle
import android.text.TextUtils
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
import com.abe.todolist.data.models.Priority

import com.abe.todolist.data.models.ToDoData

import com.abe.todolist.data.viewmodel.ToDoViewModel
import com.abe.todolist.databinding.FragmentAddBinding
import com.example.todoapp.fragments.SharedViewModel


class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                 if (menuItem.itemId == android.R.id.home) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                if (menuItem.itemId == R.id.menu_add){
                    insertDataToDB()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun insertDataToDB() {
        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        if (validated(mTitle, mDescription)){
            // insert data to db
            val newData = ToDoData(
                id = 0,
                title = mTitle,
                priority = parsePriority(mPriority),
                description = mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(),"success", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)

        }else Toast.makeText(requireContext(),"fill all the fields", Toast.LENGTH_SHORT).show()
    }

    private fun parsePriority(priority: String): Priority {
        return when(priority){
            "High Priority" -> Priority.HIGH
            "Medium Priority" -> Priority.MEDIUM
            "Low Priority" -> Priority.LOW
            else -> Priority.LOW
        }
    }

    private fun validated(mTitle:String,mDescription:String): Boolean {
       return if (TextUtils.isEmpty(mTitle) || TextUtils.isEmpty(mDescription)){

            false
        } else !(mTitle.isEmpty() || mDescription.isEmpty())
    }


}
