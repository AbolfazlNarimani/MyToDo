package com.abe.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abe.todolist.data.models.ToDoData

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertData(toDoData: ToDoData)

     @Update
     suspend fun updateData(toDoData: ToDoData)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAll()
    @Delete
    suspend fun deleteItem(toDoData: ToDoData)

    @Query("SELECT * FROM todo_table WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>>


}