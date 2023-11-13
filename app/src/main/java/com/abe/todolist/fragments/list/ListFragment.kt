package com.abe.todolist.fragments.list

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

class ListFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private lateinit var binding: FragmentListBinding
    private lateinit var recyclerView: RecyclerView
    private val adapter: ListAdapter by lazy { ListAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentListBinding.inflate(inflater)

        return this.binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

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
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    fun populateRv() {
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->

            if (data.isNotEmpty()) {
                adapter.differ.submitList(data)
            } else {
                this.binding.noDataImageView.visibility = View.VISIBLE
                this.binding.noDataTextView.visibility = View.VISIBLE
                this.binding.noDataGuide.visibility = View.VISIBLE
                this.binding.noDataGuideArrow.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpRv() {
        recyclerView = this.binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        populateRv()
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val currentItem = adapter.differ.currentList[position]
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                findNavController().navigate(action)
            }
        })
    }

}