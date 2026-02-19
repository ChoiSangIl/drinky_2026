package com.drinky.domain.dto

import com.drinky.domain.entity.CheckIn
import java.time.LocalDate

data class CalendarDayDto(
    val date: LocalDate,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean,
    val checkIn: CheckIn? = null
) {
    val isSober: Boolean? get() = checkIn?.isSober
    val hasRecord: Boolean get() = checkIn != null
}
