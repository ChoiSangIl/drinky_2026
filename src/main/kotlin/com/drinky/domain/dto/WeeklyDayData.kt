package com.drinky.domain.dto

import java.time.LocalDate

data class WeeklyDayData(
    val date: LocalDate,
    val dayOfWeekKr: String,
    val isSober: Boolean?,
    val isToday: Boolean
)
