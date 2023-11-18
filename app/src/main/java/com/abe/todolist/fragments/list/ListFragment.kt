package com.abe.todolist.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.abe.todolist.R
import com.abe.todolist.data.models.ToDoData
import com.abe.todolist.data.viewmodel.ToDoViewModel
import com.abe.todolist.databinding.FragmentListBinding
import com.abe.todolist.fragments.SharedViewModel
import com.abe.todolist.fragments.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

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
                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                    R.id.menu_delete_all -> deleteAll()
                    R.id.menu_priority_high -> {
                        mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
                            adapter.differ.submitList(it)
                            recyclerView.scheduleLayoutAnimation()
                        }
                    }

                    R.id.menu_priority_medium -> {
                        mToDoViewModel.sortByMediumPriority.observe(viewLifecycleOwner) {
                            adapter.differ.submitList(it)
                            recyclerView.scheduleLayoutAnimation()
                        }
                    }

                    R.id.menu_priority_low -> {
                        mToDoViewModel.SortByLowPriority.observe(viewLifecycleOwner) {
                            adapter.differ.submitList(it)
                            recyclerView.scheduleLayoutAnimation()
                        }
                    }
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
                recyclerView.scheduleLayoutAnimation()
            }
        }
    }

    private fun setUpRv() {
        recyclerView = this.binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        populateRv()
        recyclerView.scheduleLayoutAnimation()
        // swipe to delete
        swipeToDelete(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.differ.currentList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(deletedItem)
                Toast.makeText(
                    requireContext(),
                    "${deletedItem.title} is Deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                restoreDeletedData(
                    viewHolder.itemView,
                    deletedItem,
                    viewHolder.adapterPosition
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(
        view: View,
        deletedItem: ToDoData,
        position: Int
    ) {
        val snackbar = Snackbar.make(view, "Deleted ${deletedItem.title}", Snackbar.LENGTH_LONG)
            .setAction("undo") {
                mToDoViewModel.insertData(deletedItem)
            }.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        mToDoViewModel.searchDatabase(searchQuery).observe(this) { list ->
            list?.let {
                adapter.differ.submitList(it)
                recyclerView.scheduleLayoutAnimation()
            }
        }
    }

}