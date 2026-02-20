package com.drinky.service

import com.drinky.domain.dto.CalendarDayDto
import com.drinky.domain.entity.CheckIn
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@Service
class CalendarService(
    private val checkInService: CheckInService
) {

    data class CalendarData(
        val year: Int,
        val month: Int,
        val calendarDays: List<CalendarDayDto>,
        val prevYear: Int,
        val prevMonth: Int,
        val nextYear: Int,
        val nextMonth: Int
    )

    fun getCalendarData(userId: UUID, year: Int, month: Int): CalendarData {
        val checkIns = checkInService.getMonthlyCheckIns(userId, year, month)
        val yearMonth = YearMonth.of(year, month)
        val prev = yearMonth.minusMonths(1)
        val next = yearMonth.plusMonths(1)

        return CalendarData(
            year = year,
            month = month,
            calendarDays = buildCalendarDays(yearMonth, checkIns),
            prevYear = prev.year,
            prevMonth = prev.monthValue,
            nextYear = next.year,
            nextMonth = next.monthValue
        )
    }

    private fun buildCalendarDays(yearMonth: YearMonth, checkIns: Map<LocalDate, CheckIn>): List<CalendarDayDto> {
        val firstDay = yearMonth.atDay(1)
        val today = LocalDate.now()

        val paddingDays = if (firstDay.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDay.dayOfWeek.value
        val startDate = firstDay.minusDays(paddingDays.toLong())

        return (0 until 42).map { offset ->
            val date = startDate.plusDays(offset.toLong())
            val isCurrentMonth = date.month == yearMonth.month
            CalendarDayDto(
                date = date,
                dayOfMonth = date.dayOfMonth,
                isCurrentMonth = isCurrentMonth,
                isToday = date == today,
                isFuture = date.isAfter(today),
                checkIn = if (isCurrentMonth) checkIns[date] else null
            )
        }
    }
}
