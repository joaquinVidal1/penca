package com.example.penca.database

import androidx.room.TypeConverter
import java.time.LocalDate

object Converters {
    @TypeConverter
    fun toDate(dateString: String?): LocalDate {
        return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDate): String {
        return date.toString()
    }

}