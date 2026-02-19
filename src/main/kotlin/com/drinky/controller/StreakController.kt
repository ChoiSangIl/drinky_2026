package com.drinky.controller

import com.drinky.domain.dto.WeeklyDayData
import com.drinky.security.UserPrincipal
import com.drinky.service.CheckInService
import com.drinky.service.StreakService
import com.drinky.util.StreakMessages
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Controller
@RequestMapping("/streak")
class StreakController(
    private val streakService: StreakService,
    private val checkInService: CheckInService
) {

    @GetMapping
    fun getStreak(
        @AuthenticationPrincipal principal: UserPrincipal,
        model: Model
    ): String {
        val streak = streakService.getOrCreateStreak(principal.id)
        val justBroken = streakService.isStreakJustBroken(principal.id)
        val message = StreakMessages.getMessage(streak.currentStreak, justBroken)
        val weeklyData = getWeeklyData(principal.id)

        model.addAttribute("streak", streak)
        model.addAttribute("message", message)
        model.addAttribute("weeklyData", weeklyData)

        return "fragments/streak-display :: display"
    }

    @GetMapping("/weekly")
    fun getWeeklyCalendar(
        @AuthenticationPrincipal principal: UserPrincipal,
        model: Model
    ): String {
        val weeklyData = getWeeklyData(principal.id)
        model.addAttribute("weeklyData", weeklyData)

        return "fragments/weekly-preview :: calendar"
    }

    private fun getWeeklyData(userId: java.util.UUID): List<WeeklyDayData> {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val checkIns = checkInService.getRecentCheckIns(userId, 7)
            .associateBy { it.checkDate }

        val dayOfWeekKr = listOf("월", "화", "수", "목", "금", "토", "일")

        return (0..6).map { offset ->
            val date = monday.plusDays(offset.toLong())
            val checkIn = checkIns[date]
            WeeklyDayData(
                date = date,
                dayOfWeekKr = dayOfWeekKr[offset],
                isSober = checkIn?.isSober,
                isToday = date == today
            )
        }
    }
}
