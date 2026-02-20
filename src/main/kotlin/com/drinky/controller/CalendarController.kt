package com.drinky.controller

import com.drinky.security.UserPrincipal
import com.drinky.service.CalendarService
import com.drinky.service.CheckInService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate

@Controller
@RequestMapping("/calendar")
class CalendarController(
    private val calendarService: CalendarService,
    private val checkInService: CheckInService
) {

    @GetMapping
    fun calendarPage(
        @AuthenticationPrincipal principal: UserPrincipal,
        model: Model
    ): String {
        val now = LocalDate.now()
        addCalendarModel(model, principal.id, now.year, now.monthValue)
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
        addCalendarModel(model, principal.id, year, month)
        return "fragments/calendar-grid"
    }

    @GetMapping("/day/{date}")
    fun getDayDetail(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        model: Model
    ): String {
        model.addAttribute("date", date)
        model.addAttribute("checkIn", checkInService.getCheckIn(principal.id, date))
        model.addAttribute("editable", checkInService.isEditable(date))
        return "fragments/day-detail"
    }

    private fun addCalendarModel(model: Model, userId: java.util.UUID, year: Int, month: Int) {
        val data = calendarService.getCalendarData(userId, year, month)
        model.addAttribute("year", data.year)
        model.addAttribute("month", data.month)
        model.addAttribute("calendarDays", data.calendarDays)
        model.addAttribute("prevYear", data.prevYear)
        model.addAttribute("prevMonth", data.prevMonth)
        model.addAttribute("nextYear", data.nextYear)
        model.addAttribute("nextMonth", data.nextMonth)
    }
}
