package com.drinky.controller

import com.drinky.security.UserPrincipal
import com.drinky.service.StatisticsService
import com.drinky.util.InsightMessages
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/statistics")
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    @GetMapping
    fun statisticsPage(
        @AuthenticationPrincipal principal: UserPrincipal,
        model: Model
    ): String {
        val weeklyStats = statisticsService.getWeeklyStats(principal.id)
        val monthlyStats = statisticsService.getMonthlyStats(principal.id)
        val summaryStats = statisticsService.getSummaryStats(principal.id)
        val insight = InsightMessages.generate(summaryStats)

        model.addAttribute("weeklyStats", weeklyStats)
        model.addAttribute("monthlyStats", monthlyStats)
        model.addAttribute("stats", summaryStats)
        model.addAttribute("insight", insight)
        model.addAttribute("currentPage", "statistics")
        model.addAttribute("hasData", weeklyStats.any { it.drinkingDays > 0 || it.soberDays > 0 })

        return "pages/statistics"
    }

    @GetMapping("/weekly")
    @ResponseBody
    fun weeklyData(@AuthenticationPrincipal principal: UserPrincipal) =
        statisticsService.getWeeklyStats(principal.id)

    @GetMapping("/monthly")
    @ResponseBody
    fun monthlyData(@AuthenticationPrincipal principal: UserPrincipal) =
        statisticsService.getMonthlyStats(principal.id)
}
