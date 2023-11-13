package com.abe.todolist.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abe.todolist.R
import com.abe.todolist.data.models.Priority
import com.abe.todolist.data.models.ToDoData
import com.abe.todolist.databinding.RowLayoutBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {


    class MyViewHolder(val binding: RowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.titleText.text = differ.currentList[position].title
        holder.binding.descriptionText.text = differ.currentList[position].description
        holder.binding.dateText.text = differ.currentList[position].date
        holder.binding.timeText.text = differ.currentList[position].time

        val context = holder.binding.root.context
        val priority = differ.currentList[position].priority

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
       holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(context, getPriorityColor(priority)))
    }

    private var diffCallback = object : DiffUtil.ItemCallback<ToDoData>(){
        override fun areItemsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    private fun getPriorityColor(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> R.color.red
            Priority.MEDIUM -> R.color.yellow
            Priority.LOW -> R.color.green
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}
