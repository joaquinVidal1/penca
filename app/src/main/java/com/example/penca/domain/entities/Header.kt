package com.example.penca.domain.entities

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class Header(val text: String) {
    companion object {
        fun getHeaderText(date: LocalDate): String {
            return date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ES"))
                .replaceFirstChar { it.uppercase() } + ' ' + date.dayOfMonth + "/" + date.monthValue
        }
    }
}