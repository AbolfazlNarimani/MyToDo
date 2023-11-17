package com.abe.todolist.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abe.todolist.R
import com.abe.todolist.data.viewmodel.ToDoViewModel
import com.abe.todolist.databinding.FragmentListBinding
import com.abe.todolist.fragments.SharedViewModel

class ListFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: FragmentListBinding
    private lateinit var recyclerView: RecyclerView
    private val adapter: ListAdapter by lazy { ListAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = this.mSharedViewModel
        return this.binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMenu()
        setUpRv()

    }

    private fun setUpMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                    R.id.menu_delete_all -> deleteAll()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun deleteAll() {
        confirmRemoval {
            if (it) {
                mToDoViewModel.deleteAll()
                adapter.differ.submitList(emptyList())
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
        builder.setTitle("Delete \"ALL\"?")
        builder.setMessage("Are you sure you want to remove everything ?")
        builder.create().show()
    }

    private fun populateRv() {
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            data?.let {
                adapter.differ.submitList(it)
            }
        }
    }

    private fun setUpRv() {
        recyclerView = this.binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        populateRv()
    }

}