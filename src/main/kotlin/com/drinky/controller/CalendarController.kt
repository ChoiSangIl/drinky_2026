package com.drinky.controller

import com.drinky.domain.dto.CalendarDayDto
import com.drinky.domain.entity.CheckIn
import com.drinky.security.UserPrincipal
import com.drinky.service.CheckInService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@Controller
@RequestMapping("/calendar")
class CalendarController(
    private val checkInService: CheckInService
) {

    @GetMapping
    fun calendarPage(
        @AuthenticationPrincipal principal: UserPrincipal,
        model: Model
    ): String {
        val now = LocalDate.now()
        addCalendarData(model, principal.id, now.year, now.monthValue)
        model.addAttribute("currentPage", "calendar")
        return "pages/calendar"
    }

    @GetMapping("/{year}/{month}")
    fun getMonthCalendar(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable year: Int,
        @PathVariable month: Int,
        model: Model
    ): String {
        addCalendarData(model, principal.id, year, month)
        return "fragments/calendar-grid"
    }

    private fun addCalendarData(model: Model, userId: UUID, year: Int, month: Int) {
        val checkIns = checkInService.getMonthlyCheckIns(userId, year, month)
        val calendarDays = buildCalendarDays(year, month, checkIns)
        val yearMonth = YearMonth.of(year, month)

        model.addAttribute("year", year)
        model.addAttribute("month", month)
        model.addAttribute("calendarDays", calendarDays)
        model.addAttribute("prevYear", yearMonth.minusMonths(1).year)
        model.addAttribute("prevMonth", yearMonth.minusMonths(1).monthValue)
        model.addAttribute("nextYear", yearMonth.plusMonths(1).year)
        model.addAttribute("nextMonth", yearMonth.plusMonths(1).monthValue)
    }

    private fun buildCalendarDays(year: Int, month: Int, checkIns: Map<LocalDate, CheckIn>): List<CalendarDayDto> {
        val yearMonth = YearMonth.of(year, month)
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val today = LocalDate.now()

        val days = mutableListOf<CalendarDayDto>()

        // 이전 달 빈 칸 (일요일 시작)
        val firstDayOfWeek = firstDay.dayOfWeek
        val paddingDays = if (firstDayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfWeek.value
        for (i in paddingDays downTo 1) {
            val date = firstDay.minusDays(i.toLong())
            days.add(CalendarDayDto(
                date = date,
                dayOfMonth = date.dayOfMonth,
                isCurrentMonth = false,
                isToday = false,
                isFuture = false
            ))
        }

        // 현재 달
        for (day in 1..lastDay.dayOfMonth) {
            val date = LocalDate.of(year, month, day)
            days.add(CalendarDayDto(
                date = date,
                dayOfMonth = day,
                isCurrentMonth = true,
                isToday = date == today,
                isFuture = date.isAfter(today),
                checkIn = checkIns[date]
            ))
        }

        // 다음 달 빈 칸 (6주 채우기)
        val remaining = 42 - days.size
        for (i in 1..remaining) {
            val date = lastDay.plusDays(i.toLong())
            days.add(CalendarDayDto(
                date = date,
                dayOfMonth = date.dayOfMonth,
                isCurrentMonth = false,
                isToday = false,
                isFuture = date.isAfter(today)
            ))
        }

        return days
    }
}
