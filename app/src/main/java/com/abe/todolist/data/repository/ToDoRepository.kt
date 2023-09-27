package com.abe.todolist.data.repository

import androidx.lifecycle.LiveData
import com.abe.todolist.data.ToDoDao
import com.abe.todolist.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()

    fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }


}