package com.abe.todolist.data

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.abe.todolist.data.models.Priority

@TypeConverters
class Converter {
    @TypeConverter
    fun fromPriority(priority: Priority): String{
        return priority.name
    }
    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}