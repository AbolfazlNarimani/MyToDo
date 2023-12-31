package com.abe.todolist.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.abe.todolist.data.ToDoDao
import com.abe.todolist.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()

    val sortByHighPriority = toDoDao.sortByHighPriority()
    val sortByMediumPriority = toDoDao.sortByMediumPriority()
    val sortByLowPriority = toDoDao.sortByLowPriority()

    fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteAll() {
        toDoDao.deleteAll()
    }

    suspend fun deleteItem(toDoData: ToDoData) {
        toDoDao.deleteItem(toDoData)
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>>{
        return toDoDao.searchDatabase(searchQuery)
    }


}