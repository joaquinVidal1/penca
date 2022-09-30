package com.example.penca.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun toDate(dateString: String?): LocalDate {
            return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDate): String {
        return date.toString()
    }

}